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


## 🚩 Navodila za zagon (Maven verzija)

Projekt uporablja **Maven** za upravljanje odvisnosti. Knjižnica `mpj.jar` je vključena v mapi `lib/`.

1. **Prevajanje:** `mvn clean compile`
2. **Zagon:** `$MPJ_HOME/bin/mpjrun.sh -np 4 -cp target/classes:lib/mpj.jar Main`

## 📝 Opombe
- V celotnem `README.md` ne omenjam da izvedemo konvolucije ampak operacije. To pa zato ker blur, edge detection... že res so konvolucije ampak mirror ne moremo šteti kot konvolucijo ampak je bolj transformacija. 
- Če izberemo tudi operacijo Mirror se bo Mirror operacija vedno zadnja izvedla! Sekvenca operacij (ena za drugo v izbranem vrstnem redu) šteje le za konvolucije. Medtem ko se, če izberemo mirror, zvede vedno zadnja. 


## 🏁 Testiranje
Testiranje sem opravil na svojem osebnem računalniku:
MacBook Pro M1 Max 64Gb/2Tb. 



### Testing Table - to še naredit


| Blur                   | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,023 sec            | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,046 sec   | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,084 sec  | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,136 sec  | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,277 sec  | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,465 sec  | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 1,027 sec  | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 1,822 sec  | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 4,105 sec  | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 7,395 sec  | Time for Parallel   | Time for Distributed|

| Sharpen                | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,01 sec             | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,016 sec            | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,041 sec            | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,06 sec             | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,122 sec            | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,217 sec            | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 0,48 sec             | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 0,862 sec            | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 1,936 sec            | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 3,417 sec            | Time for Parallel   | Time for Distributed|

|  SobelX                | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,008 sec  | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,02 sec  | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,047 sec  | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,06 sec  | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,118 sec  | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,209 sec  | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 0,478 sec  | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 0,831 sec | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 1,882 sec  | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 3,311 sec  | Time for Parallel   | Time for Distributed|

| Gaussian                   | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,007 sec  | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,022 sec  | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,041 sec | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,06 sec  | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,123 sec  | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,215 sec  | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 0,487 sec | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 0,859 sec  | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 1,953 sec  | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 3,492 sec  | Time for Parallel   | Time for Distributed|

| Edge detection                  | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,009 sec  | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,016 sec  | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,037 sec  | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,057 sec  | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,118 sec  | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,208 sec  | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 0,474 sec  | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 0,838 sec  | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 1,904 sec  | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 3,320 sec  | Time for Parallel   | Time for Distributed|

| Mirror                   | Sekvenčna izvedba    | Paralelna izvedba   | Distributed izvedba |
|------------------------|----------------------|---------------------|---------------------|
| 128 x 128 Slika        | 0,001 sec  | Time for Parallel   | Time for Distributed|
| 256 x 256 Slika        | 0,004 sec  | Time for Parallel   | Time for Distributed|
| 384 x 384 Slika        | 0,005 sec  | Time for Parallel   | Time for Distributed|
| 512 x 512 Slika        | 0,011 sec  | Time for Parallel   | Time for Distributed|
| 767 x 768 Slika        | 0,017 sec  | Time for Parallel   | Time for Distributed|
| 1024 x 1024 Slika      | 0,028 sec  | Time for Parallel   | Time for Distributed|
| 1536 x 1536 Slika      | 0,061 sec  | Time for Parallel   | Time for Distributed|
| 2048 x 2048 Slika      | 0,111 sec  | Time for Parallel   | Time for Distributed|
| 3072 x 3072 Slika      | 0,249 sec  | Time for Parallel   | Time for Distributed|
| 4096 x 4096 Slika      | 0,449 sec  | Time for Parallel   | Time for Distributed|



# prevedeš
javac -cp .:$MPJ_HOME/lib/mpj.jar src/*.java -d bin

# zalaufas

$MPJ_HOME/bin/mpjrun.sh -np 4 -cp bin Main

