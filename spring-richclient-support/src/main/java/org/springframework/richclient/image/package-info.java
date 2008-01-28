/**
 * Provides a image and icon loading library using resource-bundle style key lookup.
 * This library abstracts away hardcoded image paths in your code, supports
 * at-runtime resizable icons (for accessbility), and provides built in broken
 * image indicator support.  Automatic caching of images and icons using soft
 * references is also provided.
 * <p/>
 * The design is very like Spring's MessageSource support and also builds off
 * Spring's core.io.Resource classes for accessing images from a underlying resource such
 * as a file, URL, or the classpath.
 */
package org.springframework.richclient.image;
