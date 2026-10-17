# Déploiement sur Render

L'application se déploie en **deux services** Render, décrits par `render.yaml`
à la racine du dépôt :

| Service | Type Render | Rôle |
|---|---|---|
| `decoevent-backend` | Web Service (Docker) | API Spring Boot (port fourni par `PORT`) |
| `decoevent-frontend` | Static Site | Interface Angular compilée |

La base de données MySQL est **externe** (Render ne propose que PostgreSQL).

---

## Prérequis

1. Un compte Render.
2. Le dépôt poussé sur GitHub/GitLab.
3. Une base **MySQL accessible depuis Internet** (étape 0 ci-dessous).

---

## 0. Créer la base MySQL sur Aiven (recommandé)

> ⚠️ **Render ne propose que PostgreSQL en base managée.** L'application étant
> en MySQL, la base doit être externe. **Aiven** est le meilleur choix : offre
> MySQL gratuite **sans limite de durée** et **sans carte bancaire**.

1. Créer un compte sur <https://console.aiven.io/signup> (GitHub ou Google).
2. **Create service** → **MySQL** → plan **Free**.
3. Choisir une région **proche de `frankfurt`** (région du backend Render), par
   exemple une région européenne — cela limite la latence.
4. Attendre que le service soit `RUNNING`, puis onglet **Users** : relever
   l'**hôte**, le **port**, l'**utilisateur** et le **mot de passe**.
5. Onglet **Networking** → **Add IP address** → `0.0.0.0/0` (Render n'a pas
   d'IP de sortie fixe sur l'offre gratuite).

La base créée par défaut par Aiven s'appelle **`defaultdb`**, et le port n'est
**pas** 3306 (Aiven utilise un port personnalisé, souvent `12xxx`).

> ℹ️ Aiven peut **éteindre** un service gratuit resté longtemps inactif (tu es
> prévenu par email avant). Il suffit de cliquer sur **Power on** dans la console
> Aiven. À vérifier avant une démonstration.
>
> ⚠️ Si la base est éteinte au moment du déploiement, le backend ne démarrera
> pas et le déploiement Render échouera au health check. Rallume-la d'abord.

Autres fournisseurs possibles : [Clever Cloud](https://www.clever-cloud.com),
[Railway](https://railway.app).

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
| `DB_URL` | `jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | utilisateur Aiven (souvent `avnadmin`) |
| `DB_PASSWORD` | mot de passe Aiven |
| `CORS_ORIGIN` | `https://decoevent-frontend.onrender.com` |
| `JWT_SECRET` | généré automatiquement par Render |
| `DDL_AUTO` | `update` au 1er déploiement, puis `validate` |

`sslMode=REQUIRED` chiffre la connexion sans vérifier le certificat : c'est ce
qui convient au certificat d'Aiven, et cela évite d'avoir à embarquer une AC.
Remplacer `defaultdb` si le nom de base diffère.

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
- `frontend/scripts/postbuild.js` crée le `index.html` manquant (le build SSR
  n'émet que `index.csr.html`, or un hébergement statique a besoin d'un
  `index.html`).
- Le backend autorise cette origine grâce à `CORS_ORIGIN` (plusieurs origines
  possibles, séparées par des virgules).

### Schéma de base et `DDL_AUTO`

Le profil `prod` est en `ddl-auto: validate` par défaut : Hibernate vérifie le
schéma mais ne le crée pas. Sur une base vide, **le démarrage échoue**. D'où
`DDL_AUTO=update` pour le premier déploiement.

Solution propre à terme : ajouter **Flyway** (ou Liquibase) et des migrations,
puis repasser à `validate` définitivement.

### Offres gratuites et ressources

- Le backend gratuit (**0,1 CPU / 512 Mo**) s'endort après 15 min sans trafic :
  le premier appel après réveil prend environ 1 minute, et le démarrage à froid
  est lent sur 0,1 CPU.
- Le `Dockerfile` force `-XX:MaxRAMPercentage=70` : le réglage JVM par défaut
  (25 % de 512 Mo ≈ 128 Mo de tas) est trop juste pour Spring Boot.
- Les **Static Sites** sont gratuits et ne se mettent pas en veille.
- Quota : 750 h d'instance gratuite par mois. Un backend tournant en continu
  consomme ~730 h/mois : ça passe, mais sans marge.

---

## Dépannage

| Symptôme | Cause probable |
|---|---|
| Redémarrages en boucle, `Schema-validation: missing table` | Base vide + `DDL_AUTO=validate` → passer à `update` |
| Base injoignable après quelques semaines | Service Aiven éteint pour inactivité → **Power on** dans la console Aiven |
| `Communications link failure` | IP non autorisée dans Aiven → Networking → ajouter `0.0.0.0/0` |
| Déploiement en échec au health check | Base Aiven éteinte pendant le build → la rallumer puis redéployer |
| Erreur CORS dans la console navigateur | `CORS_ORIGIN` ≠ URL exacte du frontend (schéma + domaine, sans `/` final) |
| `Access denied for user` | Identifiants Aiven erronés, ou base non encore `RUNNING` |
| Le frontend appelle `/api` ou `localhost` | `API_URL` absente au moment du build → la définir puis redéployer |
| Échec du build Angular (Node) | `NODE_VERSION` < `22.12.0` |
| Conteneur tué (`OOMKilled`) | Tas JVM trop large → ajuster `JAVA_OPTS` |
| `Cannot connect to Docker daemon` en local | Normal si Docker Desktop est arrêté ; Render gère le build |

## Test local de l'image (facultatif)

```bash
cd Backend
docker build -t decoevent-backend .

# --add-host : permet au conteneur de joindre le MySQL installé sur la machine hôte.
docker run --rm -p 8070:8070 \
  --add-host=host.docker.internal:host-gateway \
  -e SPRING_PROFILES_ACTIVE=local \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/decoevent?useSSL=false&serverTimezone=UTC" \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=1234 \
  decoevent-backend
```
