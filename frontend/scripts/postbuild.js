/**
 * Rend le build exploitable sur un hébergement statique (Render Static Site).
 *
 * Le build Angular en mode SSR (outputMode: "server") n'émet que
 * "index.csr.html" : le "index.html" est normalement généré à la volée par le
 * serveur SSR. Un Static Site ne fait pas tourner ce serveur, il a donc besoin
 * d'un vrai fichier index.html à la racine des fichiers publiés.
 */
const fs = require('node:fs');
const path = require('node:path');

const browserDir = path.join(__dirname, '..', 'dist', 'gestion-prestataire', 'browser');
const source = path.join(browserDir, 'index.csr.html');
const cible = path.join(browserDir, 'index.html');

if (fs.existsSync(cible)) {
  console.log('index.html déjà présent — rien à faire.');
} else if (fs.existsSync(source)) {
  fs.copyFileSync(source, cible);
  console.log('index.html créé depuis index.csr.html.');
} else {
  console.error(`Fichier introuvable : ${source}`);
  process.exit(1);
}
