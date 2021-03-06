alter table dossiers add COLUMN deposant_nom varchar(255);
alter table dossiers add COLUMN deposant_prenom varchar(255);
alter table dossiers add COLUMN deposant_profils varchar(255);
insert into dossiers (deposant_nom, deposant_prenom, deposant_profils) select deposant_id as deposant_nom,deposant_id as deposant_prenom,'DEPOSANT,BETA' as deposant_profils from dossiers;
alter table dossiers drop COLUMN deposant_email;
alter table messages add COLUMN auteur text;
alter table messages alter COLUMN contenu type text;
insert into messages (auteur) select ('User={identite={Personne={id={' || auteur_id || '}, email={' || auteur_email || '}, nom={auteur_id}, prenom={auteur_id}, profils={INSTRUCTEUR}') as auteur from messages;
alter table messages drop COLUMN auteur_id;
alter table messages drop COLUMN auteur_email;