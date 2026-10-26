
CREATE TABLE client (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom        VARCHAR(100) NOT NULL,
    prenom     VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    telephone  VARCHAR(20),
    adresse    VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE produit (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(150) NOT NULL,
    description     VARCHAR(500),
    prix            DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    quantite_stock  INT NOT NULL DEFAULT 0 CHECK (quantite_stock >= 0)
) ENGINE=InnoDB;

CREATE TABLE commande (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_commande  DATE NOT NULL,
    statut         VARCHAR(30) NOT NULL DEFAULT 'EN_COURS',
    client_id      BIGINT NOT NULL,
    CONSTRAINT fk_commande_client FOREIGN KEY (client_id)
        REFERENCES client(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Commande (1) ---- (1..*) LigneCommande (0..*) ---- (1) Produit
CREATE TABLE ligne_commande (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    commande_id    BIGINT NOT NULL,
    produit_id     BIGINT NOT NULL,
    quantite       INT NOT NULL CHECK (quantite > 0),
    prix_unitaire  DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_ligne_commande FOREIGN KEY (commande_id)
        REFERENCES commande(id) ON DELETE CASCADE,
    CONSTRAINT fk_ligne_produit FOREIGN KEY (produit_id)
        REFERENCES produit(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------- Données de test ----------
INSERT INTO client (nom, prenom, email, telephone, adresse) VALUES
 ('elrroud',  'Yassine', 'yassineelarroud@mail.ma', '0612345678', 'Tanger, Hawma chouk'),
 ('elarroud', 'Salma',   'salma.elarroud@mail.ma',  '0623456789', 'Tétouan, Sania drml'),
 ('elarroud', 'Omar',    'omar.elarroud@mail.ma',   '0634567890', 'Tanger, Boukhalef');

INSERT INTO produit (nom, description, prix, quantite_stock) VALUES
 ('Clavier mécanique', 'Clavier AZERTY rétroéclairé', 450.00, 25),
 ('Souris sans fil',   'Souris optique 1600 DPI',     150.00, 60),
 ('Écran 24"',         'Écran Full HD IPS',          1490.00, 10),
 ('Casque audio',      'Casque Bluetooth',            320.00, 30);

INSERT INTO commande (date_commande, statut, client_id) VALUES
 ('2026-10-20', 'LIVREE',   1),
 ('2026-10-24', 'EN_COURS', 2);

INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES
 (1, 1, 1, 450.00),
 (1, 2, 2, 150.00),
 (2, 3, 1, 1490.00);
