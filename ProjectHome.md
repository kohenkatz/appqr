Enables to generate single QR code for apps which are on both Android Market and iPhone AppStore.

> Created for and deployed into Google App Engine, using their API. Really small and simple Java Servlet. It detects device from User Agent header in HTTP request and forwards user to your specified URL, that's it.

Usage:
  1. Check out URL-s for your application downloads: typically one for Android Market and another for iOs App Store. You can use also any other download location. **Now also Windows Phone, BlackBerry and Symbian options added.**
  1. go to http://www.appqr.mobi
  1. enter URLs there
  1. Get URL for QRCode image. Use this image to promote your app.

You will get two functionally identical QR code versions: one which does not depend on any other service, but has longer URL and more detailed (harder to read) QR code. Second one uses URL shortening and therefore depends on external free service is.gd for this. Second one is easier and faster for reading by phone cameras and I would suggest using this, unless this external URL shortening service has issues or you do not trust it for any reason.

Both images use Google Charts API and depend on it.

The service and soft is simple and free, no ads, no registration needed etc. It is provided without any warranty through.