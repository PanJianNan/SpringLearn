/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yabadun.support;

import com.yabadun.util.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PathMatchingResourcePatternResolver{

//	private static final Log logger = LogFactory.getLog(PathMatchingResourcePatternResolver.class);
//
//	private static Method equinoxResolveMethod;
//
//	static {
//		// Detect Equinox OSGi (e.g. on WebSphere 6.1)
//		try {
//			Class<?> fileLocatorClass = PathMatchingResourcePatternResolver.class.getClassLoader().loadClass(
//					"org.eclipse.core.runtime.FileLocator");
//			equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
//			logger.debug("Found Equinox FileLocator for OSGi bundle URL resolution");
//		}
//		catch (Throwable ex) {
//			equinoxResolveMethod = null;
//		}
//	}


//	private final ResourceLoader resourceLoader;
//
	private PathMatcher pathMatcher = new AntPathMatcher();
	private static final String CLASSPATH_ALL_URL_PREFIX = "classpath:*";
	private static final String CLASSPATH_URL_PREFIX = "classpath:";


	/**
	 * Return the PathMatcher that this resource pattern resolver uses.
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}


	public URL[] getResources(String locationPattern) throws IOException {
		if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
			// a class path resource (multiple resources for same name possible)
			if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
				// a class path resource pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// all class path resources with the given name
				return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
			}
		}
		else {
			// Only look for a pattern after a prefix here
			// (to not get fooled by a pattern symbol in a strange prefix).
			int prefixEnd = locationPattern.indexOf(":") + 1;
			if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
				// a file pattern
				return findPathMatchingResources(locationPattern);
			}
			else {
				// a single resource with the given name
				if (locationPattern.startsWith(CLASSPATH_URL_PREFIX)) {
					String path = locationPattern.substring(CLASSPATH_URL_PREFIX.length());
					path = path.startsWith("/") ? path.substring(1) : path;
					return new URL[]{this.getClass().getClassLoader().getResource(path)};
				} else {
					throw new RuntimeException(String.format("暂不支持这个格式的路径配置[%s]", locationPattern));
				}
			}
		}
	}

	/**
	 * Find all class location resources with the given location via the ClassLoader.
	 * @param location the absolute path within the classpath
	 * @return the result as Resource array
	 * @throws IOException in case of I/O errors
	 * @see ClassLoader#getResources
	 * @see #convertClassLoaderURL
	 */
	protected URL[] findAllClassPathResources(String location) throws IOException {
		String path = location;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		Enumeration<URL> resourceUrls = this.getClass().getClassLoader().getResources(path);
		Set<URL> result = new LinkedHashSet<URL>(16);
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			result.add(convertClassLoaderURL(url));
		}
		return result.toArray(new URL[result.size()]);
	}

	/**
	 * Convert the given URL as returned from the ClassLoader into a Resource object.
	 * <p>The default implementation simply creates a UrlResource instance.
	 * @param url a URL as returned from the ClassLoader
	 * @return the corresponding Resource object
	 * @see ClassLoader#getResources
	 */
	protected URL convertClassLoaderURL(URL url) {
		return url;
	}

	/**
	 * Find all resources that match the given location pattern via the
	 * Ant-style PathMatcher. Supports resources in jar files and zip files
	 * and in the file system.
	 * @param locationPattern the location pattern to match
	 * @return the result as Resource array
	 * @throws IOException in case of I/O errors
	 * @see #doFindPathMatchingJarResources
	 * @see #doFindPathMatchingFileResources
	 */
	protected URL[] findPathMatchingResources(String locationPattern) throws IOException {
		String rootDirPath = determineRootDir(locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		URL[] rootDirResources = getResources(rootDirPath);
		Set<URL> result = new LinkedHashSet<URL>(16);
		for (URL rootDirResource : rootDirResources) {
			rootDirResource = resolveRootDirResource(rootDirResource);
			if (isJarResource(rootDirResource)) {
				result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
			}
			else if (rootDirResource.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirResource, subPattern, getPathMatcher()));
			}
			else {
				result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
			}
		}
		return result.toArray(new URL[result.size()]);
	}

	/**
	 * Determine the root directory for the given location.
	 * <p>Used for determining the starting point for file matching,
	 * resolving the root directory location to a {@code java.io.File}
	 * and passing it into {@code retrieveMatchingFiles}, with the
	 * remainder of the location as pattern.
	 * <p>Will return "/WEB-INF/" for the pattern "/WEB-INF/*.xml",
	 * for example.
	 * @param location the location to check
	 * @return the part of the location that denotes the root directory
	 * @see #retrieveMatchingFiles
	 */
	protected String determineRootDir(String location) {
		int prefixEnd = location.indexOf(":") + 1;
		int rootDirEnd = location.length();
		while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
			rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
		}
		if (rootDirEnd == 0) {
			rootDirEnd = prefixEnd;
		}
		return location.substring(0, rootDirEnd);
	}

	/**
	 * Resolve the specified resource for path matching.
	 * <p>The default implementation detects an Equinox OSGi "bundleresource:"
	 * / "bundleentry:" URL and resolves it into a standard jar file URL that
	 * can be traversed using Spring's standard jar file traversal algorithm.
	 * @param original the resource to resolve
	 * @return the resolved resource (may be identical to the passed-in resource)
	 * @throws IOException in case of resolution failure
	 */
	protected URL resolveRootDirResource(URL original) throws IOException {
		throw new RuntimeException(String.format("暂不支持这个格式的路径配置[%s]", original.getPath()));
		//todo
//		return original;
	}

	/**
	 * Return whether the given resource handle indicates a jar resource
	 * that the {@code doFindPathMatchingJarResources} method can handle.
	 * <p>The default implementation checks against the URL protocols
	 * "jar", "zip" and "wsjar" (the latter are used by BEA WebLogic Server
	 * and IBM WebSphere, respectively, but can be treated like jar files).
	 * @param resource the resource handle to check
	 * (usually the root directory to start path matching from)
	 * @see #doFindPathMatchingJarResources
	 */
	protected boolean isJarResource(URL resource) throws IOException {
		return ResourceUtils.isJarURL(resource);
	}

	/**
	 * Find all resources in jar files that match the given location pattern
	 * via the Ant-style PathMatcher.
	 * @param rootDirResource the root directory as Resource
	 * @param subPattern the sub pattern to match (below the root directory)
	 * @return the Set of matching Resource instances
	 * @throws IOException in case of I/O errors
	 * @see JarURLConnection
	 */
	protected Set<URL> doFindPathMatchingJarResources(URL rootDirResource, String subPattern)
			throws IOException {

		URLConnection con = rootDirResource.openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if (con instanceof JarURLConnection) {
			// Should usually be the case for traditional JAR files.
			JarURLConnection jarCon = (JarURLConnection) con;
			ResourceUtils.useCachesIfNecessary(jarCon);
			jarFile = jarCon.getJarFile();
			jarFileUrl = jarCon.getJarFileURL().toExternalForm();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
		}
		else {
			// No JarURLConnection -> need to resort to URL file parsing.
			// We'll assume URLs of the format "jar:path!/entry", with the protocol
			// being arbitrary as long as following the entry format.
			// We'll also handle paths with and without leading "file:" prefix.
			String urlFile = rootDirResource.getFile();
			int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
			if (separatorIndex != -1) {
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + ResourceUtils.JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			}
			else {
				jarFile = new JarFile(urlFile);
				jarFileUrl = urlFile;
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				// Root entry path must end with slash to allow for proper matching.
				// The Sun JRE does not return a slash here, but BEA JRockit does.
				rootEntryPath = rootEntryPath + "/";
			}
			Set<URL> result = new LinkedHashSet<URL>(8);
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath.length());
					if (getPathMatcher().match(subPattern, relativePath)) {
//						result.add(rootDirResource.createRelative(relativePath));
						//todo
						throw new RuntimeException(String.format("暂不支持这个格式的路径配置[%s]", rootDirResource.getPath()));
					}
				}
			}
			return result;
		}
		finally {
			// Close jar file, but only if freshly obtained -
			// not from JarURLConnection, which might cache the file reference.
			if (newJarFile) {
				jarFile.close();
			}
		}
	}

	/**
	 * Resolve the given jar file URL into a JarFile object.
	 */
	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
			try {
				return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
			}
			catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever happen).
				return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
			}
		}
		else {
			return new JarFile(jarFileUrl);
		}
	}

	/**
	 * Find all resources in the file system that match the given location pattern
	 * via the Ant-style PathMatcher.
	 * @param rootDirResource the root directory as Resource
	 * @param subPattern the sub pattern to match (below the root directory)
	 * @return the Set of matching Resource instances
	 * @throws IOException in case of I/O errors
	 * @see #retrieveMatchingFiles
	 */
	protected Set<URL> doFindPathMatchingFileResources(URL rootDirResource, String subPattern)
			throws IOException {

		File rootDir = null;
//		try {
////			rootDir = rootDirResource.getFile().getAbsoluteFile();
//		}
//		catch (IOException ex) {
//
//			return Collections.emptySet();
//		}
		return doFindMatchingFileSystemResources(rootDir, subPattern);
	}

	/**
	 * Find all resources in the file system that match the given location pattern
	 * via the Ant-style PathMatcher.
	 * @param rootDir the root directory in the file system
	 * @param subPattern the sub pattern to match (below the root directory)
	 * @return the Set of matching Resource instances
	 * @throws IOException in case of I/O errors
	 * @see #retrieveMatchingFiles
	 */
	protected Set<URL> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
		Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
		Set<URL> result = new LinkedHashSet<URL>(matchingFiles.size());
		for (File file : matchingFiles) {
			result.add(new URL(file.getAbsolutePath()));
		}
		return result;
	}

	/**
	 * Retrieve files that match the given path pattern,
	 * checking the given directory and its subdirectories.
	 * @param rootDir the directory to start from
	 * @param pattern the pattern to match against,
	 * relative to the root directory
	 * @return the Set of matching File instances
	 * @throws IOException if directory contents could not be retrieved
	 */
	protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
		if (!rootDir.exists()) {
			// Silently skip non-existing directories.

			return Collections.emptySet();
		}
		if (!rootDir.isDirectory()) {
			// Complain louder if it exists but is no directory.

			return Collections.emptySet();
		}
		if (!rootDir.canRead()) {

			return Collections.emptySet();
		}
		String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
		if (!pattern.startsWith("/")) {
			fullPattern += "/";
		}
		fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
		Set<File> result = new LinkedHashSet<File>(8);
		doRetrieveMatchingFiles(fullPattern, rootDir, result);
		return result;
	}

	/**
	 * Recursively retrieve files that match the given pattern,
	 * adding them to the given result list.
	 * @param fullPattern the pattern to match against,
	 * with prepended root directory path
	 * @param dir the current directory
	 * @param result the Set of matching File instances to add to
	 * @throws IOException if directory contents could not be retrieved
	 */
	protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			return;
		}
		for (File content : dirContents) {
			String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
			if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
				if (!content.canRead()) {
				}
				else {
					doRetrieveMatchingFiles(fullPattern, content, result);
				}
			}
			if (getPathMatcher().match(fullPattern, currPath)) {
				result.add(content);
			}
		}
	}


	/**
	 * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
	 */
	private static class VfsResourceMatchingDelegate {
		public static Set<URL> findMatchingResources(
				URL rootResource, String locationPattern, PathMatcher pathMatcher) throws IOException {
			Object root = VfsPatternUtils.findRoot(rootResource);
			PatternVirtualFileVisitor visitor =
					new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), locationPattern, pathMatcher);
			VfsPatternUtils.visit(root, visitor);
			return visitor.getResources();
		}
	}


	/**
	 * VFS visitor for path matching purposes.
	 */
	@SuppressWarnings("unused")
	private static class PatternVirtualFileVisitor implements InvocationHandler {

		private final String subPattern;

		private final PathMatcher pathMatcher;

		private final String rootPath;

		private final Set<URL> resources = new LinkedHashSet<URL>();

		public PatternVirtualFileVisitor(String rootPath, String subPattern, PathMatcher pathMatcher) {
			this.subPattern = subPattern;
			this.pathMatcher = pathMatcher;
			this.rootPath = (rootPath.length() == 0 || rootPath.endsWith("/") ? rootPath : rootPath + "/");
		}

//		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (Object.class.equals(method.getDeclaringClass())) {
				if (methodName.equals("equals")) {
					// Only consider equal when proxies are identical.
					return (proxy == args[0]);
				}
				else if (methodName.equals("hashCode")) {
					return System.identityHashCode(proxy);
				}
			}
			else if ("getAttributes".equals(methodName)) {
				return getAttributes();
			}
			else if ("visit".equals(methodName)) {
				visit(args[0]);
				return null;
			}
			else if ("toString".equals(methodName)) {
				return toString();
			}

			throw new IllegalStateException("Unexpected method invocation: " + method);
		}

		public void visit(Object vfsResource) {
//			if (this.pathMatcher.match(this.subPattern,
//					VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length()))) {
//				this.resources.add(new VfsResource(vfsResource));
//			}
		}

		public Object getAttributes() {
			return VfsPatternUtils.getVisitorAttribute();
		}

		public Set<URL> getResources() {
			return this.resources;
		}

		public int size() {
			return this.resources.size();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("sub-pattern: ").append(this.subPattern);
			sb.append(", resources: ").append(this.resources);
			return sb.toString();
		}
	}

}
