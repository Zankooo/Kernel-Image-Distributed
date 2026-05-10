# 🖼️  Kernel Image Processing (Porazdeljena izvedba)

Kernel image processing je temeljna tehnika računalniškega vida, kjer sliko obdelamo tako, da čez njo “drsi” majhen filter (kernel) in na vsakem pikslu izračuna novo vrednost na podlagi pikslov ki so okoli njega - v njegovi okolici. To je osnova za ogromno realnih funkcij: zamegljevanje - blur(odstranjevanje šuma), ostrenje - sharpen (poudarjanje detajlov), zaznavanje robov (npr. Sobel/edge detection), izboljšanje kontrasta in pripravo slike za nadaljnjo analizo. Ker so kerneli hitri, predvidljivi in dobro delujejo na različnih tipih slik, se uporabljajo praktično povsod — od kamer na telefonih in Instagram/CapCut filtrov, do medicinskega slikanja, industrijske kontrole kakovosti, OCR/scan izboljšav, pa tudi kot “prvi korak” v pipeline-u za bolj napredne metode, kot so modeli za prepoznavanje objektov in segmentacijo.

## 🧩 Kaj program dela?
Mi kot uporabnik programa damo programu eno ali več slik svojih poljubnih slik (lahko izbiramo tudi med slikami, ki so prednaložene že v programu). Nato izberemo katero oziroma katere operacije želimo da se izvedejo na vsaki od izbranih slik. Lahko izberemo eno operacijo lahko jih izberemo več. In potem program na vsaki od teh slik izvede izbrane operacije.

## 🧪 Primeri uporabe (Use Case)

### 1. Primer uporabe
- Izberemo sliko `2048x2048-Slika.jpg`. 
- Izberemo operacije blur in mirror. (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj izbrano sliko` 
- V mapi `ustvarjene slike` se nam pojavi rezultat
- V konzoli pa tudi lahko vidimo katere operacije so se zgodile na kateri sliki, v kakšnem vrstnem redu, in tudi koliko je program potreboval da je naredil vse operacije nad izbranimi slikami!

### 2. Primer uporabe
- Izberemo sliko `2048x2048-Slika.jpg`
- Izberemo operacije; blur, edge detection in sharpen (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj izbrano sliko` 
- V mapi `ustvarjene slike` se nam pojavi rezultat
- V konzoli pa tudi lahko vidimo katere operacije so se zgodile na kateri sliki, v kakšnem vrstnem redu, in tudi koliko je program potreboval da je naredil vse operacije nad izbranimi slikami!

### 3. Primer uporabe
- Izberemo operacije; blur, edge detection in sharpen (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj mapo slik` in izberemo mapo v kateri so neke slike
- Izberemo to mapo in 
- V mapi `ustvarjene slike` se nam pojavi rezultat (za vsako od teh slik se je naredila sekvenca izbranih operacij)
- V konzoli pa tudi lahko vidimo katere operacije so se zgodile na kateri sliki, v kakšnem vrstnem redu, in tudi koliko je program potreboval da je naredil vse operacije nad izbranimi slikami!


## 🚩 Navodila za zagon

Projekt uporablja **Maven** za upravljanje odvisnosti in **MPJ Express** za porazdeljeno izvajanje.

### 1. Priprava okolja
Za delovanje programa moraš imeti na računalniku nameščeno naslednje:

#### A) Java JDK 21
Program zahteva različico Java 21.
- **Mac (z Homebrew):** `brew install openjdk@21`
- **Windows/Ostalo:** Prenesi z [Adoptium (Temurin 21)](https://adoptium.net/temurin/releases/?version=21).
- **Preverjanje:** V terminal vpiši `java -version`. Moralo bi izpisati `openjdk version "21.x.x"`.

#### B) Maven
Maven se uporablja za zbiranje (build) projekta.
- **Mac (z Homebrew):** `brew install maven`
- **Windows:** 
  1. Prenesi "Binary zip archive" z [uradne strani](https://maven.apache.org/download.cgi).
  2. Razpakiraj ga (npr. v `C:\maven`).
  3. Dodaj pot do `bin` mape (npr. `C:\maven\bin`) v sistemsko spremenljivko **Path**:
     - Išči "Environment Variables" v Start meniju.
     - Pod "System variables" najdi **Path**, klikni **Edit** -> **New** -> prilepi pot do `bin` mape.
- **Preverjanje:** V terminal vpiši `mvn -version`.

#### C) MPJ Express (Ključno za porazdeljeno delovanje)
1. **Prenos:** Na [mpjexpress.org](http://mpjexpress.org/download.php) prenesi zadnjo različico `mpj-v0_44.zip`
2. **Razpakiranje:** Unzippaj datoteko.
3. **Nastavitev okoljske spremenljivke `MPJ_HOME`(ni nujno vendar priporičljivo):**
   - **Mac/Linux:** Odpri datoteko `~/.zshrc` (ali `~/.bash_profile`) in dodaj:
     ```bash
     export MPJ_HOME=/pot/do/tvoje/mape/mpj-v0_44
     export PATH=$MPJ_HOME/bin:$PATH
     ```
     Nato v terminalu zaženi `source ~/.zshrc`.
   - **Windows:**
     - Desni klik na "This PC" -> Properties -> Advanced System Settings -> Environment Variables.
     - Pod "System Variables" klikni **New**. Name: `MPJ_HOME`, Value: `C:\pot\do\mpj-v0_44`.
     - Najdi spremenljivko `Path`, klikni **Edit** -> **New** -> dodaj `%MPJ_HOME%\bin`.
4. **Preverjanje:** V terminalu vpiši `echo $MPJ_HOME` (Mac) ali `echo %MPJ_HOME%` (Windows).

### 2. Prevajanje in zagon
V korenski mapi projekta izvedi:

1. **Prevajanje:** 
   ```bash
   mvn clean compile
   ```
2. **Zagon:** 
   Program lahko zaženeš na dva načina (izbereš tistega, ki ti boljši):

   **A) Če imaš nastavljen `MPJ_HOME` (naj bi bila boljša strategija):**
   ```bash
   $MPJ_HOME/bin/mpjrun.sh -np 4 -cp target/classes:lib/mpj.jar Main
   ```

   **B) Če nimaš nastavljene poti (uporabiš direktno pot):**
   *Zamenjaj `/pot/do/mpj` z dejansko potjo.*
   ```bash
   /pot/do/mpj/bin/mpjrun.sh -np 4 -cp target/classes:lib/mpj.jar Main
   ```

ampak zakaj moras pol rocno naloziti mpj express ce je v maven to opisano??

To je odlično vprašanje! Gre za razliko med prevajanjem in izvajanjem:

Za prevajanje (Maven): Maven potrebuje samo datoteko mpj.jar, da ve, kateri ukazi obstajajo. V tvojem pom.xml vidiš, da je ta knjižnica nastavljena kot system odvisnost, kar pomeni: "Maven, ne išči tega na spletu, poglej v mojo mapo lib/mpj.jar." To zadošča, da ukaz mvn clean compile uspešno ustvari tvoj program.
Za izvajanje (MPJ Express Runtime): MPI ni navadna knjižnica, ampak celotno okolje. Da tvoj program dejansko teče na več procesih, potrebuje zaganjalnik (mpjrun.sh), ki zna komunicirati z operacijskim sistemom, odpirati vrata (ports) za komunikacijo in upravljati z več procesi hkrati. Tega dela Maven ne more opraviti, ker so to sistemske skripte in ne Java koda.
V tvojem primeru:

Maven poskrbi, da tvoj program "ve", kako se pogovarjati z MPI.
MPJ Express (ki ga naložiš ročno) pa je "motor", ki tvoj program dejansko požene in omogoči to pogovarjanje.
Če bi MPJ Express obstajal kot standardna Maven knjižnica (ki bi vključevala tudi zaganjalnike), bi ga Maven morda lahko naložil sam, vendar je MPJ specifično orodje, ki zahteva namestitev na nivoju operacijskega sistema.







## 📝 Opombe
- **POMEMBNO:** Za delovanje so potrebni vsaj **2 procesa** (parameter `-np 2` ali več). Prvi proces (Master) upravlja z GUI, ostali pa opravljajo delo. Če program zaženeš z manj kot dvema procesoma, se obdelava ne bo izvedla.
- V celotnem `README.md` ne omenjam da izvedemo konvolucije ampak operacije. To pa zato ker blur, edge detection... že res so konvolucije ampak mirror ne moremo šteti kot konvolucijo ampak je bolj transformacija. 
- Če izberemo tudi operacijo Mirror se bo Mirror operacija vedno zadnja izvedla! Sekvenca operacij (ena za drugo v izbranem vrstnem redu) šteje le za konvolucije. Medtem ko se, če izberemo mirror, zvede vedno zadnja. 


## 🏁 Testiranje
Testiranje sem opravil na svojem osebnem računalniku:
MacBook Pro M1 Max 64Gb/2Tb. 



### Testing Table


| Blur                   | Sekvenčna izvedba    | Paralelna izvedba (9 jeder)   | Distributed izvedba |
|------------------------|----------------------|-------------------------------|---------------------|
| 128 x 128 Slika        | 0,023 sec            | 0,004 sec                        | 0,004 sec |
| 256 x 256 Slika        | 0,046 sec            | 0,008 sec                       | 0,006 sec |
| 384 x 384 Slika        | 0,084 sec            | 0,019 sec                      | 0,008 sec |
| 512 x 512 Slika        | 0,136 sec            | 0,028 sec                      | 0,013 sec |
| 767 x 768 Slika        | 0,277 sec            | 0,056 sec                       | 0,022 sec |
| 1024 x 1024 Slika      | 0,465 sec            | 0,113 sec                        | 0,038 sec |
| 1536 x 1536 Slika      | 1,027 sec            | 0,204 sec                        | 0,085 sec |
| 2048 x 2048 Slika      | 1,822 sec            | 0,363 sec                        | 0,151 sec |
| 3072 x 3072 Slika      | 4,105 sec            | 0,887 sec                        | 0,334 sec|
| 4096 x 4096 Slika      | 7,395 sec            | 1,493 sec                         | 0,592 sec|

| Sharpen                | Sekvenčna izvedba    | Paralelna izvedba (9 jeder) | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,01 sec             | 0,004 sec           | 0,003 sec|
| 256 x 256 Slika        | 0,016 sec            | 0,007 sec           | 0,004 sec|
| 384 x 384 Slika        | 0,041 sec            | 0,011 sec           | 0,006 sec|
| 512 x 512 Slika        | 0,06 sec             | 0,021 sec           | 0,009 sec|
| 767 x 768 Slika        | 0,122 sec            | 0,047 sec           | 0,017 sec|
| 1024 x 1024 Slika      | 0,217 sec            | 0,071 sec           | 0,028 sec|
| 1536 x 1536 Slika      | 0,48 sec             | 0,18 sec            | 0,064 sec|
| 2048 x 2048 Slika      | 0,862 sec            | 0,304 sec           | 0,117 sec|
| 3072 x 3072 Slika      | 1,936 sec            | 0,733 sec           | 0,260 sec|
| 4096 x 4096 Slika      | 3,417 sec            | 1,241 sec           | 0,465 sec|

|  SobelX                | Sekvenčna izvedba    | Paralelna izvedba (9 jeder)  | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,008 sec            | 0,003 sec           | 0,002 sec |
| 256 x 256 Slika        | 0,02 sec             | 0,008 sec           | 0,004 sec|
| 384 x 384 Slika        | 0,047 sec            | 0,016 sec           | 0,006 sec|
| 512 x 512 Slika        | 0,06 sec             | 0,026 sec           | 0,010 sec|
| 767 x 768 Slika        | 0,118 sec            | 0,04 sec            | 0,018 sec|
| 1024 x 1024 Slika      | 0,209 sec            | 0,076 sec           | 0,032 sec |
| 1536 x 1536 Slika      | 0,478 sec            | 0,171 sec           | 0,068 sec|
| 2048 x 2048 Slika      | 0,831 sec            | 0,284 sec           | 0,125 sec|
| 3072 x 3072 Slika      | 1,882 sec            | 0,679 sec           | 0,272 sec|
| 4096 x 4096 Slika      | 3,311 sec            | 1,245 sec           | 0,489 sec|

| Gaussian               | Sekvenčna izvedba    | Paralelna izvedba (9 jeder)  | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,007 sec            | 0,003 sec           | 0,003 sec|
| 256 x 256 Slika        | 0,022 sec            | 0,007 sec           | 0,003 sec|
| 384 x 384 Slika        | 0,041 sec            | 0,016 sec           | 0,006 sec|
| 512 x 512 Slika        | 0,06 sec             | 0,02 sec            | 0,010 sec|
| 767 x 768 Slika        | 0,123 sec            | 0,04 sec            | 0,016 sec|
| 1024 x 1024 Slika      | 0,215 sec            | 0,083 sec           | 0,028 sec|
| 1536 x 1536 Slika      | 0,487 sec            | 0,16 sec            | 0,063 sec|
| 2048 x 2048 Slika      | 0,859 sec            | 0,295 sec           | 0,112 sec|
| 3072 x 3072 Slika      | 1,953 sec            | 0,694 sec           | 0,253 sec|
| 4096 x 4096 Slika      | 3,492 sec            | 1,259 sec           | 0,448 sec|

| Edge detection         | Sekvenčna izvedba    | Paralelna izvedba (9 jeder)  | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,009 sec            | 0,003 sec           | 0,002 sec|
| 256 x 256 Slika        | 0,016 sec            | 0,008 sec           | 0,004 sec|
| 384 x 384 Slika        | 0,037 sec            | 0,015 sec           | 0,006 sec|
| 512 x 512 Slika        | 0,057 sec            | 0,025 sec           | 0,009 sec|
| 767 x 768 Slika        | 0,118 sec            | 0,052 sec           | 0,018 sec|
| 1024 x 1024 Slika      | 0,208 sec            | 0,07 sec            | 0,031 sec|
| 1536 x 1536 Slika      | 0,474 sec            | 0,172 sec           | 0,072 sec|
| 2048 x 2048 Slika      | 0,838 sec            | 0,325 sec           | 0,128 sec|
| 3072 x 3072 Slika      | 1,904 sec            | 0,753 sec           | 0,279 sec|
| 4096 x 4096 Slika      | 3,320 sec            | 1,305 sec           | 0,515 sec |

| Mirror                   | Sekvenčna izvedba      | Paralelna izvedba (9 jeder)   |
|------------------------|--------------------------|---------------------
| 128 x 128 Slika        | 0,001 sec  | 0,004 sec   | 
| 256 x 256 Slika        | 0,004 sec  | 0,008 sec   | 
| 384 x 384 Slika        | 0,005 sec  | 0,006 sec   | 
| 512 x 512 Slika        | 0,011 sec  | 0,009 sec   | 
| 767 x 768 Slika        | 0,017 sec  | 0,016 sec   | 
| 1024 x 1024 Slika      | 0,028 sec  | 0,027 sec   | 
| 1536 x 1536 Slika      | 0,061 sec  | 0,062 sec   | 
| 2048 x 2048 Slika      | 0,111 sec  | 0,112 sec   | 
| 3072 x 3072 Slika      | 0,249 sec  | 0,249 sec   | 
| 4096 x 4096 Slika      | 0,449 sec  | 0,446 sec   | 



