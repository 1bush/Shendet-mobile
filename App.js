import { registerServiceWorker } from './register-sw';

// Register PWA service worker
registerServiceWorker();

// Detect standalone mode (installed as PWA)
const isStandalone = window.matchMedia('(display-mode: standalone)').matches 
  || window.navigator.standalone 
  || document.referrer.includes('android-app://');

// You can use this to hide browser-specific UI when in standalone mode
if (isStandalone) {
  document.body.classList.add('standalone');
}
