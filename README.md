# Gestion des commandes — Atelier 1 JEE (MVC + Servlet / JSP / Java Bean)

Application web de gestion des commandes de plusieurs clients, développée avec les standards JEE
(Servlet, JSP + JSTL, Java Beans), le patron MVC, Maven, Tomcat et MySQL.

**Stack :** Java 17 · Jakarta EE 10 (Servlet 6.0 / web module 6.0) · JSTL 3.0 · CDI Weld (`@Inject`, `@ApplicationScoped`) · Tomcat 10.1 · MySQL 8 · Maven

---

## 1. Prérequis

| Outil | Version | Remarque |
|---|---|---|
| JDK | 17 ou + | |
| IntelliJ IDEA | Ultimate (support Jakarta EE / Tomcat intégré) | Community : voir §4-bis |
| Apache Tomcat | **10.1.x** | Tomcat 9 ne marche pas (javax ≠ jakarta) |
| MySQL | 8.x | ou XAMPP / WAMP (MariaDB) |

## 2. Base de données (Étape 2)

Dans MySQL Workbench, phpMyAdmin ou en ligne de commande :

```bash
mysql -u root -p < sql/schema.sql
```

Le script crée la base `gestion_commandes`, les 4 tables et des données de test.
Adaptez ensuite **`src/main/resources/db.properties`** :

```properties
db.url=jdbc:mysql://localhost:3306/gestion_commandes?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=VOTRE_MOT_DE_PASSE
```

## 3. Ouvrir le projet dans IntelliJ IDEA

1. **File → Open…** → choisir le dossier `gestion-commandes` (celui qui contient `pom.xml`) → *Open as Project*.
2. IntelliJ détecte Maven et télécharge les dépendances (barre de progression en bas). Si besoin : panneau **Maven** (à droite) → bouton ⟳ *Reload All Maven Projects*.
3. **File → Project Structure → Project** : SDK = JDK 17+.

> **Créer le projet vous-même au lieu de l'ouvrir (Étape 3 de l'énoncé) :**
> *File → New → Project → Jakarta EE* → Template **Web application**, Application server **Tomcat 10.1**,
> Build system **Maven**, Language Java → *Next* → Version **Jakarta EE 10** (Servlet 6.0, donc web module > 3) → *Create*.
> IntelliJ crée directement un projet Maven : il ne reste qu'à ajouter `mysql-connector-j` dans `pom.xml`
> (sous Eclipse, il faut créer un *Dynamic Web Project* puis *Configure → Convert to Maven Project*).

## 4. Configurer Tomcat et lancer

1. **Run → Edit Configurations… → + → Tomcat Server → Local**.
2. *Application server* → **Configure…** → indiquer le dossier d'installation de Tomcat 10.1.
3. Onglet **Deployment → + → Artifact… → `gestion-commandes:war exploded`**.
   Mettre *Application context* à **`/gestion-commandes`**.
4. Onglet **Server** : *On 'Update' action* = **Redeploy**, *On frame deactivation* = **Update classes and resources** (rechargement rapide des JSP).
5. **Run ▶** → le navigateur s'ouvre sur <http://localhost:8080/gestion-commandes/>.

### 4-bis. IntelliJ Community (sans intégration Tomcat)

```bash
mvn clean package
```
puis copier `target/gestion-commandes.war` dans `TOMCAT/webapps/` et lancer `TOMCAT/bin/startup.bat` (ou `.sh`).

## 5. Architecture (MVC)

```
src/main/java/ma/fstt/
├── entities/   Java Beans : Client, Produit, Commande, LigneCommande        (Modèle)
├── service/    Interfaces : IDao<T>, IClientDao, IProduitDao,
│               ICommandeDao, ILigneCommandeDao                               (Modèle)
├── dao/        Implémentations JDBC : *DaoImpl (@ApplicationScoped)          (Modèle)
├── util/       DBConnection (lit db.properties)
└── web/        Servlets : Accueil, Client, Produit, Commande                 (Contrôleur)
src/main/webapp/
├── index.jsp                    → redirige vers /accueil
├── css/style.css
└── WEB-INF/
    ├── web.xml, beans.xml       (beans.xml active CDI)
    └── views/                   JSP + JSTL, protégées sous WEB-INF          (Vue)
        ├── common/ header.jsp, footer.jsp
        ├── client/ list, form, details
        ├── produit/ list, form
        └── commande/ list, form, details
```

Flux d'une requête : **navigateur → Servlet (contrôleur) → DAO via interface (`@Inject`) → MySQL → Java Beans placés dans la requête → `forward` vers une JSP (JSTL) → HTML**.

## 6. Fonctionnalités (Étape 5)

| Espace | URL | Opérations |
|---|---|---|
| Client | `/clients` | lister, rechercher, ajouter, modifier, supprimer, fiche + commandes du client |
| Produit | `/produits` | lister, rechercher, ajouter, modifier, supprimer (refusé si le produit est dans une commande) |
| Commande | `/commandes` | lister avec total, créer, modifier (client/date/statut), supprimer ; ajouter / modifier la quantité / retirer des lignes |

Règles métier : le prix unitaire est figé dans la ligne au moment de la commande ; le stock est décrémenté à l'ajout d'une
ligne (refus si insuffisant) et restauré quand une ligne ou une commande est supprimée — chaque fois dans une transaction JDBC.

## 7. Problèmes fréquents

| Symptôme | Cause / solution |
|---|---|
| `ClassNotFoundException: javax.servlet...` | Vous utilisez Tomcat 9 → passez à Tomcat **10.1** |
| `Access denied for user 'root'` | Mauvais mot de passe dans `db.properties` |
| `Communications link failure` | MySQL n'est pas démarré (ou port ≠ 3306) |
| `NullPointerException` sur un DAO dans une Servlet | CDI non actif : vérifier `WEB-INF/beans.xml` et la dépendance `weld-servlet-shaded` |
| Accents illisibles (`Ã©`) | Ré-exécuter `schema.sql` (il contient `SET NAMES utf8mb4`) |

## 8. Publier sur GitHub

```bash
git init
git add .
git commit -m "Atelier 1 JEE - gestion des commandes (MVC, Servlet, JSP, JSTL)"
git branch -M main
git remote add origin https://github.com/<votre-compte>/gestion-commandes-jee.git
git push -u origin main
```
