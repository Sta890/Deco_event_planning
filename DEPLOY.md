# Déploiement sur Render

L'application se déploie en **deux services** Render, décrits par `render.yaml`
à la racine du dépôt :

| Service | Type Render | Rôle |
|---|---|---|
| `decoevent-backend` | Web Service (Docker) | API Spring Boot (port fourni par `PORT`) |
| `decoevent-frontend` | Static Site | Interface Angular compilée |

---

## Prérequis

1. Un compte Render.
2. Le dépôt poussé sur GitHub/GitLab.
3. Une base **MySQL accessible depuis Internet**.

> ⚠️ **Render ne propose que PostgreSQL en base managée.** L'application étant
> en MySQL, il faut une base externe : [Aiven](https://aiven.io) (offre
> gratuite), [Clever Cloud](https://www.clever-cloud.com) ou
> [Railway](https://railway.app).

Récupérer auprès du fournisseur : **hôte, port, nom de base, utilisateur, mot de
passe**, et activer l'**accès public** (Render n'a pas d'IP fixe sur l'offre
gratuite).

---

## 1. Pousser le code

```bash
git add .
git commit -m "chore: préparation du déploiement Render"
git push origin main
```

## 2. Créer les services

Render → **New +** → **Blueprint** → sélectionner le dépôt → **Apply**.

Render lit `render.yaml` et propose les deux services. Les variables marquées
`sync: false` sont à renseigner dans le dashboard.

## 3. Variables d'environnement

### Backend (`decoevent-backend`)

| Variable | Valeur |
|---|---|
| `DB_URL` | `jdbc:mysql://HOST:PORT/DBNAME?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | utilisateur MySQL |
| `DB_PASSWORD` | mot de passe MySQL |
| `CORS_ORIGIN` | `https://decoevent-frontend.onrender.com` |
| `JWT_SECRET` | généré automatiquement par Render |
| `DDL_AUTO` | `update` au 1er déploiement, puis `validate` |

Retirer `requireSSL=true` si le fournisseur MySQL n'impose pas TLS.

### Frontend (`decoevent-frontend`)

| Variable | Valeur |
|---|---|
| `API_URL` | `https://decoevent-backend.onrender.com/api` |
| `NODE_VERSION` | `22.12.0` |

## 4. Ordre des opérations (dépendances croisées)

1. Créer le **backend**, renseigner `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`, attendre la fin du déploiement.
2. Reporter l'URL du backend dans `API_URL` du **frontend**, puis redéployer le frontend.
3. Reporter l'URL du frontend dans `CORS_ORIGIN` du **backend**, puis redéployer le backend.

## 5. Vérification

```bash
curl https://decoevent-backend.onrender.com/api/health
# -> {"status":"UP"}
```

Puis ouvrir le frontend et se connecter.

---

## Détails techniques

### Pourquoi Docker pour le backend ?

Java n'est pas un *runtime* natif chez Render. Le `Backend/Dockerfile` fait une
compilation Maven puis copie le jar dans une image JRE Alpine. Le contexte de
build est `Backend/`.

### Pourquoi une URL d'API absolue côté frontend ?

En développement, `apiUrl = '/api'` est relayé par `proxy.conf.json` vers
`localhost:8070`. Un **Static Site** ne sait pas proxifier : le build injecte
donc l'URL absolue du backend via `API_URL`.

- `frontend/scripts/set-env.js` régénère `src/environments/environment.production.ts`.
- `angular.json` (configuration `production`) remplace `environment.ts` par ce fichier.
- Le backend autorise cette origine grâce à `CORS_ORIGIN` (plusieurs origines
  possibles, séparées par des virgules).

### Schéma de base et `DDL_AUTO`

Le profil `prod` est en `ddl-auto: validate` par défaut : Hibernate vérifie le
schéma mais ne le crée pas. Sur une base vide, **le démarrage échoue**. D'où
`DDL_AUTO=update` pour le premier déploiement.

Solution propre à terme : ajouter **Flyway** (ou Liquibase) et des migrations,
puis repasser à `validate` définitivement.

### Offres gratuites

- Le backend gratuit s'endort après ~15 min d'inactivité : le premier appel
  après réveil prend 30 à 60 s.
- Les Static Sites sont gratuits, sans mise en veille.

---

## Dépannage

| Symptôme | Cause probable |
|---|---|
| Redémarrages en boucle, `Schema-validation: missing table` | Base vide + `DDL_AUTO=validate` → passer à `update` |
| Erreur CORS dans la console navigateur | `CORS_ORIGIN` ≠ URL exacte du frontend (schéma + domaine, sans `/` final) |
| `Access denied for user` | Identifiants MySQL, ou IP Render non autorisée chez le fournisseur |
| Le frontend appelle `/api` ou `localhost` | `API_URL` absente au moment du build → la définir puis redéployer |
| Échec du build Angular (Node) | `NODE_VERSION` < `22.12.0` |
| `Cannot connect to Docker daemon` en local | Normal si Docker Desktop est arrêté ; Render gère le build |

## Test local de l'image (facultatif)

```bash
cd Backend
docker build -t decoevent-backend .
docker run --rm -p 8070:8070 \
  -e SPRING_PROFILES_ACTIVE=local \
  decoevent-backend
```
