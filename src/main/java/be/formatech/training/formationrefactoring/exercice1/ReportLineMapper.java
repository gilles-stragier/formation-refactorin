package be.formatech.training.formationrefactoring.exercice1;

import java.util.ArrayList;
import java.util.List;

import static be.formatech.training.formationrefactoring.exercice1.Util.getNossEndingQuarterDate;

public class ReportLineMapper {

    private final EmployeurDao employeurDao;

    public ReportLineMapper(EmployeurDao employeurDao) {
        this.employeurDao = employeurDao;
    }

    /**
     * Construction d'une liste de messages d'anomalies sur base du contenu du message contenu dans une ligne de la table
     * OnssAnomalie. Un message de la liste résultat tient en une seule ligne.
     *
     * @param anomalie contenu d'une cellule de la table OnssAnomalie
     * @return La liste des messages d'anomalie.
     */
    static List<String> splitAnomalyTextArea(String anomalie) {

        List<String> anomalies = new ArrayList<>();
        String[] anomalieLines = anomalie.split("\n");

        for (int i = 0; i < anomalieLines.length; i++) {
            StringBuilder oneLine = new StringBuilder();
					/*
						En fonction du fait que l'anomalie ait été produite par du code post refactoring Vauban de 2017 ou
						du code antérieur, il faut déterminer le libellé qui sera rapporté.

						Pour une version du code antérieure au refactoring Vauban, il n'y a à priori qu'une erreur par ligne
						de la table ONSSANOMALIE, mais le libellé de l'erreur peut tenir en plusieurs lignes. Il faut dans ce cas
						lire toutes les lignes consécutives en de la même erreur en une fois et supprimer les retours à la ligne.
					 */
            String currentLibelle = anomalieLines[i].trim();

            if (anomalie.matches(AnomalyRecord.ANOMALIES_PATTERN)) {
                // Code post refactoring Vauban 2017
                oneLine.append(currentLibelle);
            } else {
                // Code pré refactoring Vauban 2017
                // Dans ce code on peut retrouver une erreur sur plusieurs lignes
                oneLine.append(currentLibelle);
                // Lire toutes les lignes qui font partie de la même erreur pour n'en faire qu'une seule.
                // Dans les messages sur plusieurs ligne, la deuxième ligne et les lignes suivantes ne sont pas sencées contenir
                // les mots INFO, WARNING et ERROR ni en majuscule, ni en minuscule...
                // Donc on boucle tant qu'on ne retrouve pas une telle ligne, puis comme on
                // a lu une ligne de trop, on revient en arrière d'une ligne (une sorte de "put back").
                i++;
                while (i < anomalieLines.length && !anomalieLines[i].toUpperCase().contains(AnomalyRecord.ERROR)
                        && !anomalieLines[i].toUpperCase().contains(AnomalyRecord.WARNING) && !anomalieLines[i].toUpperCase().contains(AnomalyRecord.INFO)) {
                    oneLine.append(" ").append(anomalieLines[i].replace("\r", "").replace("\n", "").trim());
                    i++;
                }
                i--;
            }
            anomalies.add(oneLine.toString());
        }
        return anomalies;
    }

    List<ReportLine> buildReportLines(Trimestre trimestre, List<AnomalyRecord> anomalyRecords) {
        List<ReportLine> reportLines = new ArrayList<>();

        for (AnomalyRecord anomalyRecord : anomalyRecords) {
            if (anomalyRecord.getAnomaly() == null || anomalyRecord.getAnomaly().length() == 0) {
                continue;
            }

            // Génération du contenu des lignes du fichier intermédiaire correspondant à la ligne du ResultSet
            // et écriture de ces lignes dans le fichier.
            List<String> anomalies = splitAnomalyTextArea(anomalyRecord.getAnomaly());

            for (String anomaly : anomalies) {

                String rejCat = AnomalyRecord.INFO;
                String libelleAnomaly = null;

                if (anomalyRecord.getAnomaly().matches(AnomalyRecord.ANOMALIES_PATTERN)) {
                    libelleAnomaly = anomaly.split("-\\d{3,5}##")[1];

                    if (AnomalyRecord.WARNING.equals(anomaly.split("-\\d{3,5}##")[0]) || AnomalyRecord.ERROR.equals(anomaly.split("-\\d{3,5}##")[0])) {
                        rejCat = AnomalyRecord.ERROR;
                    }
                } else {
                    libelleAnomaly = anomaly;
                    if (anomaly.toUpperCase().contains(AnomalyRecord.ERROR)) {
                        rejCat = AnomalyRecord.ERROR;
                    } else if (anomaly.toUpperCase().contains(AnomalyRecord.WARNING)) {
                        rejCat = AnomalyRecord.WARNING;
                    }
                }

                ReportLine rep = new ReportLine(trimestre, anomalyRecord, rejCat, libelleAnomaly, employeurDao.fetchEmpName(getNossEndingQuarterDate(trimestre.asYYYYNNShort()), anomalyRecord.getEmpcode()));
                reportLines.add(rep);

                rep.setStatut(computeStatut(anomalyRecord, rep));
            }

        }
        return reportLines;
    }

    private String computeStatut(AnomalyRecord record, ReportLine reportLine) {
        /* Traduction des statuts du lot ou de la société */
        String olStat = record.getStatut();
        String olStatActu = record.getStatutActualisation();
        String olcStat = record.getOlc_statut();
        String olcStatActu = record.getOlc_statutActualisation();

        String statut = null;

        if (record.getEmpcode() != null && record.getEmpcode().trim().length() != 0) {
            // ligne société, travailleur ou contrat
            if ("O".equals(record.getLottype())) { // lot orig.
                if ("05".equals(olcStat)) {
                    // Implique que la société est à (re)générer
                    statut = "à corriger";
                } else {

                    if ("01".equals(olStat)) {
                        // Implique que la société est à (re)générer
                        statut = "à générer";
                    } else if ("04".equals(olStat)) {
                        // Impliqu'une des sociétés du lot est en cours de génération,
                        // donc au pire la société est à regénérer
                        statut = "en cours";
                    } else if ("03".equals(olStat)) {
                        // Impliqu'une ou plusieurs des sociétés du lot sont en erreur
                        if ("03".equals(olcStat)) { // société en erreur ...
                            statut = "rejet";
                        } else if ("02".equals(olcStat)) {
                            statut = "généré";
                        } else { // ... sinon à regénérer
                            statut = "à générer";
                        }
                    } else if ("02".equals(olStat)) {
                        // Implique toutes sociétés du lot sont générées
                        statut = "généré";
                    } else if ("09".equals(olStat)) {
                        statut = "envoi en cours";
                    } else if ("08".equals(olStat)) {
                        statut = "envoi en erreur";
                    } else if ("10".equals(olStat)) {
                        statut = "envoyé";
                    } else if ("21".equals(olStat)) {
                        statut = "accusé négatif";
                    } else if (",20,31,30,".indexOf("," + olStat + ",") != -1) {
                        // une ou plusieurs sociétés du lot ont été soit ...
                        if ("30".equals(olcStat)) {
                            statut = "notification positive ";
                        } else if ("31".equals(olcStat)) {
                            statut = "notification négative";
                        } else if ("40".equals(olcStat)) {
                            statut = "révoqué";
                        } else {
                            statut = "accusé positif"; // pas de noti reçue
                        }
                        // Ce qui suit écrase la valeur précèdente de 'rejCat'
                        // qui ne peut qu'être moins relevante
                        if ("40".equals(olcStat)) {
                            reportLine.setRejCat(AnomalyRecord.ERROR);
                        }
                    } else {
                        statut = "?"; // cas imprévu !
                    }
                }
                statut += " (" + record.getStatut() + "/" + record.getOlc_statut() + ")";
            } else { // lot rectif.
                if ("00".equals(olStat)) {
                    statut = "consultation "; // phase 1 : consultation de la déclaration actuelle chez l'ONSS
                    if ("101".equals(olcStatActu)) {
                        statut += "à faire";
                    } else if ("103".equals(olcStatActu)) {
                        statut += "- envoi en cours";
                    } else if ("105".equals(olcStatActu)) {
                        statut += "- envoi en erreur";
                    } else if ("109".equals(olcStatActu)) {
                        statut += "envoyée";
                    } else if ("123".equals(olcStatActu)) {
                        statut += "- réception en cours";
                    } else if ("125".equals(olcStatActu)) {
                        statut += "- réception en erreur";
                    } else if (",127,128,129,".indexOf("," + olcStatActu + ",") != -1) {
                        statut += "reçue";
                    } else {
                        statut = "?"; // autres cas
                    }
                } else if (",101,104,103,102,".indexOf(olStat) != -1) {
                    statut = "actualisation "; // phase 2 : générer la déclaration actuelle chez ForHRM (même
                    // forme que l'original)
                    if ("101".equals(olcStat)) {
                        statut += "à faire";
                    } else if ("104".equals(olcStat)) {
                        statut += "en cours";
                    } else if ("103".equals(olcStat)) {
                        statut += "en erreur";
                    } else if ("102".equals(olcStat)) {
                        statut += "faite";
                    } else {
                        statut = "?"; // autres cas
                    }
                } else if ("01,04,03,02,".contains(olStat)) {
                    statut = "génération "; // phase 3 : comparer la déclaration de ForHRM avec celle de l'ONSS
                    // pour générer la déclaration
                    // rectificative à envoyer
                    if ("01".equals(olcStat)) {
                        statut += "à faire";
                    } else if ("04".equals(olcStat)) {
                        statut += "en cours";
                    } else if ("03".equals(olcStat)) {
                        statut += "en erreur";
                    } else if ("02".equals(olcStat)) {
                        if ("200".equals(olcStatActu)) {
                            statut += "complète faite";
                        } else if ("201".equals(olcStatActu)) {
                            statut += "partielle faite";
                        } else if ("209".equals(olcStatActu)) {
                            statut += "- rien à rectifier";
                        } else if ("210".equals(olcStatActu)) {
                            statut += "- complète CNL uniquement";
                        } else {
                            statut = "?"; // cas imprévu !
                        }

                    } else {
                        statut = "?"; // autres cas
                    }
                }
                // phase 4 : envoi
                else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé négatif";
                } else if (",20,33,32,31,30,".contains("," + olStat + ",")) {
                    // une ou plusieurs sociétés du lot ont été soit ...
                    if ("33".equals(olcStat)) {
                        statut = "notification partielle négative";
                    } else if ("32".equals(olcStat)) {
                        statut = "notication partielle positive";
                    } else if ("31".equals(olcStat)) {
                        statut = "notication négative";
                    } else if ("30".equals(olcStat)) {
                        statut = "notication positive";
                    } else if ("40".equals(olcStat)) {
                        statut = "révoqué";
                    } else {
                        statut = "accusé positive";
                    }
                    // Ce qui suit écrase la valeur précèdente de 'rejCat'
                    // qui ne peut qu'être moins relevante
                    if ("40".equals(olcStat)) {
                        reportLine.setRejCat(AnomalyRecord.ERROR);
                    }
                } else {
                    statut = "?"; // cas imprévu !
                }

            }
        } else if (record.getLotNo() != null && record.getLotNo().trim().length() != 0) {
            // ligne lot ou indice
            if ("O".equals(record.getLottype())) { // lot orig.
                if ("01".equals(olStat)) {
                    // Implique que toutes les sociétés sont à (re)générer
                    statut = "à générer";
                } else if ("04".equals(olStat)) {
                    // Impliqu'une des sociétés du lot est en cours de génération
                    statut = "génération en cours";
                } else if ("03".equals(olStat)) {
                    // Impliqu'une ou plusieurs des sociétés du lot sont en erreur
                    statut = "rejet";
                } else if ("02".equals(olStat)) {
                    // Implique toutes sociétés du lot sont générées
                    statut = "généré";
                } else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé -";
                } else if ("20".equals(olStat)) {
                    // Implique que des notifications de société du lot sont attendues mais pas toutes reçues
                    statut = "accusé +";
                } else if ("31".equals(olStat)) {
                    // Implique que les notifications des sociétés du lot sont toutes reçues dont quelques unes
                    // sont négatives
                    statut = "sociétés à révoquer";
                } else if ("30".equals(olStat)) {
                    // Implique que les notifications des sociétés du lot sont toutes reçues dont les négatives
                    // sont révoquées
                    statut = "terminé";
                } else {
                    statut = "?"; // cas imprévu !
                }
                statut += " (" + record.getStatut() + ")";
            } else { // lot rectif.
                // phase 1 : consultation de la déclaration actuelle chez l'ONSS
                if ("00".equals(olStat)) {
                    statut = "consultation ";
                }
                // phase 2 : générer la déclaration actuelle chez ForHRM (même forme que l'original)
                else if ("101".equals(olStat)) {
                    statut = "actualisation à faire";
                } else if ("104".equals(olStat)) {
                    statut = "actualisation en cours";
                } else if ("103".equals(olStat)) {
                    statut = "actualisation en erreur";
                } else if ("102".equals(olStat)) {
                    statut = "actualisation faite";
                }
                // phase 3 : comparer la déclaration de ForHRM avec celle de l'ONSS pour générer la déclaration
                // rectificative à envoyer
                else if ("01".equals(olStat)) {
                    statut = "génération à faire";
                } else if ("04".equals(olStat)) {
                    statut = "génération en cours";
                } else if ("03".equals(olStat)) {
                    statut = "génération en erreur";
                } else if ("02".equals(olStat)) {
                    if ("200".equals(olStatActu)) {
                        statut = "génération complète faite";
                    } else if ("201".equals(olStatActu)) {
                        statut = "génération partielle faite";
                    } else if ("209".equals(olStatActu)) {
                        statut = "rien à rectifier";
                    } else if ("210".equals(olcStatActu)) {
                        statut += "- complète CNL uniquement";
                    } else {
                        statut = "?"; // cas imprévu !
                    }
                }
                // phase 4 : envoi
                else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé négatif";
                } else if ("20".equals(olStat)) {
                    statut = "accusé positif";
                } else if ("33".equals(olStat)) {
                    statut = "notification partielle négative";
                } else if ("32".equals(olStat)) {
                    statut = "notication partielle positive";
                } else if ("31".equals(olStat)) {
                    statut = "notication négative";
                } else if ("30".equals(olStat)) {
                    statut = "notication positive";
                } else {
                    statut = "?"; // cas imprévu !
                }
                statut += " (" + record.getStatut() + "," + record.getStatutActualisation() + ")";
            }
        }
        return statut;
    }
}
