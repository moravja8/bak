import GUI.MainFrame;
import Utils.KnimeLogSetupUtil;
import dbConnectors.H2Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        H2Connector.getInstance();
        /* FIXME
        try {
            KnimeLogSetupUtil.addBatchExecutorLogger();
        } catch (IOException e) {
            log.error("Could not load log settings of Knime", e);
        }
        */
        MainFrame mf = new MainFrame();
        mf.init();


    }
}

/** Transformace uživatelských dotazů pro analýzu dat v průmyslové automatizaci
 *
 * Cílem práce je navrhnout způsob transformace dotazů pro analýzu velkého objemu sémanticky popsaných senzorických dat
 * s využitím nástrojů KNIME a platformy pro Big Data. Data jsou popsána pomocí existující ontologie SHS.
 * Práce bude zahrnovat implementaci komponenty pro analýzu dat a je plánována jako součást existujícího Semantic Big Data Historianu.
 */

/*  DONE nacist seznam tabulek z hivu
    Predpokladam, ze seznam tabulek pude vypsat pomoci sql
    a) prikonektit se primo na hive pomoci odbc connectoru - primejsi, muze byt tezsi - nakonec zvoleno, command lina fungovala, ale bylo to hrozne pomale (y)
    b) vytvorit specialni wf ktere mi bude akorat spoustet sql dotazy v hivu a vracet vysledek - trosku workaround, prijde mi to ale rozumne - nezkouseno
    c) komunikace s hivem probiha skry prikazovou radku, implementace jednoducha, vzkonavani prikazu muze byt potencionalne pomalejsi (n)- pomale
*/

/*  DONE generovani zakladniho sql a nahrani do exekutoru
    a) pomoci service
    b) pokud by se pouzival konektor, muze byt na konektoru (y)
*/

/*  udelat vyhodnocovac rozdeleni dotazu - CostsMapper a CostsEvaluater
    a) vyhodnocovat primo spustenim obou casti separe - nevyhoda: wf se musi spusti, jedna se o aposteriorni pristup
        - melo by jit dobre - knime na log level debug vypisuje cas behu jednotlivych nodu, zatim se mi to ale nepodarilo propsat do aplikace
        - jiz funguje vypis casu pro beh jednotlivych nodu, ukladano do h2 db, zatim nijak neanalyzovano
    b) vyhodnocovat apriorne - otestovat rychlost hivu na dane operaci na x radcich, stejne tak knimu, potom odhadnout slozitost obou
       casti realneho wf (tohle bude hard) a nejak zkalkulovat
    c) vyhodnocovat apriorne pomoci explain, pokud hive a knime neco takoveho maji
        - hive explain je rule based, cost based explain je diskutovan napr na jire https://issues.apache.org/jira/browse/HIVE-1938 ,
        ale vypada to, ze se vyvoj zastavil
    ...
    TODO pokud tabulka neexistuje, vytvorit
    sql pro vytvoreni h2 tabulky
    create table costs(
	executedAt 	varchar2(22),
	database_name 	varchar2(50),
	table_name 	varchar2(50),
	workflow_name	varchar2(100),
	node_name		varchar2(50),
	node_nbr		integer,
	duration		integer
)


    DONE v ramci testovani vyhodnocovace by bylo vhodne nahrat nejaka vhodna data do knimu
*/

//  DONE udelat gui

//  TODO dopsat javadoc

//  TODO vytvorit junit testy

/* TODO OpenPoint
    - jakym zpusobem se bude zobrazovat vysledek vraceny spustenym wf - bude ulozeny v souboru, na ktery bude vypsana cesta
    - aplikace bude rikat jen jak je aktualni rozlozeni vykonne nebo bude i navrhovat upravu? - neni v rozsahu bp
    - bude do vyhodnocovani zahrnuta aktualni vytizenost hive serveru? - neni v rozsahu bp
*/

// TODO merger wf
// muze doslova mergovat ruzna wf - je asi mozne, ale je nutne davat pozor na hromadu veci
// muze jen spojovat metanody - asi lepsi, nutno otestovat
// nebyl nalezen sw, ktery toto dela
// - pri sestavovani wf z metanodu je potreba odmazat nody bez vstupni hrany
// -

//TODO nastaveni aplikace - properties

// DONE sunchronizovat nastaveni Hive connectoru v aplikaci a v knihovne

// DONE jednotna zmena ciloveho adresare - pomoci property

// DONE - ulozeni vystupu nodu musi byt deklarovano explicitne nejakum writerem




