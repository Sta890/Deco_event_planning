/**
 * Génère src/environments/environment.production.ts.
 *
 * La variable d'environnement API_URL est injectée par la plateforme de
 * déploiement (ex. Render, pendant le build d'un Static Site). Elle doit
 * contenir l'URL absolue du backend, sans barre oblique finale.
 *
 *   API_URL=https://decoevent-backend.onrender.com/api npm run config
 *
 * Sans API_URL on retombe sur "/api" (build local, ou backend servi sur la
 * même origine).
 */
const fs = require('node:fs');
const path = require('node:path');

const apiUrl = (process.env.API_URL || '/api').replace(/\/+$/, '');
const target = path.join(__dirname, '..', 'src', 'environments', 'environment.production.ts');

const contenu = `// Fichier généré par scripts/set-env.js — ne pas éditer à la main.
export const environment = {
  production: true,
  apiUrl: '${apiUrl}'
};
`;

fs.writeFileSync(target, contenu, 'utf8');
console.log(`environment.production.ts généré (apiUrl = ${apiUrl})`);
