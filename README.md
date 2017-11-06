 Raz dla jednego z projektów prowadzonego przez firmę w któej obecnie pracuję, rozważaliśmy możliwość wybudowania źródła energii nie zależnego od dostaw z sieci zewnętrznej, zdolnego do zasilania odbiorców w 100% w systemie pracy wyspowej. Ok. można tutaj przyjąć założenie że w oparciu o elektrownię wiatrową budujemy magazyn energii w postacji baterii akumulatorów i ładujemy je kiedy mamy energię z wiatru, a kiedy jej nie mamy to bierzemy zmagazynowaną w akumulatorach.

Co jendak w sytuacji kiedy całą energię zgromadzoną w bateriach mamy już pobraną a wiatr jak nie wiał - tak nie wieje??

Założyliśmy wtedy że musimy wybudować źródło wytwórcze które w przypadku braku energii wyprodukowanej w elektrowni wiatrowej, będzie w stanie szybko się załączyć i produkować energię.

Ok. kolejne bardzo dobre założenie tylko.. tylko jak sprawdzić ile taka elektrownia będzie pracować?? Na rynku jest wiele programów pozwalających na obliczenie mocy generowanej przez elektrownię wiatrową, jednakże ponieważ wyniki tych analiz są następnie przedkładane do analiz finansowych, obliczenia te sprowadzane są dla okresu pełnego roku, i dają one informację o łącznej wyprodukowanej energii elektrycznej. W związku z tym potrzebowałem narzędzia które na podstawie danych pomiarowych, powie mi ile nasza elektrownia wiatrowa wyprodukuje energii elektrycznej dla danej prędkości wiatru.

Pierwszą wersją programu, był program działający w konsoli a który to:

    miał wprowadzoną jedną krzywą mocy dla wybranej dla mnie turbiny wiatrowej,
    nazwy plików z danymi pomiarowymi były wprowadzane bezpośrednio w kodzie programu
    wyniki obliczeń były generowane bezpośrednio do pliku txt, któy można było następnie odpalić w excelu i szybko zrobić podsumowanie

Widać wyraźnie że taka wersja jest może i dobra ale jako rozwiązanie tymczasowe do zastosowania doraźnego, no bo przecież nie o to chodzi żeby ułatawiając sobie pracę jednocześnie ją komplikować i wszystkie dane zawsze wprowadzać ręcznie a do tego nie mieć możliwości wyboru elektrowni wiatrowej.

W obecnej wersji programu, na chwilę obecną wprowadzonych jest tylko 10 modeli elektrowni wiatrowych wraz z ich krzywymi mocy (krzywa mocy – charakterystyka generowanej energii elektrycznej w zależności od średniej prędkości wiatru) oraz różnymi wysokościami wieży elektrowni wiatrowej. W miarę wolnego czasu postaram się bardziej rozbudować bazę danych ponieważ zamierzam rozbudować tą aplikacje o inne moduły.

Na chwilę obecną program przyjmuje tylko dane pomiarowe zapisane w pliku tekstowym, bez sprawdzania ich poprawności (sprawdzanie poprawności danych na pewno zostanie jeszcze wprowadzone do programu).

Po wprowadzaniu danych do programu, możemy przystąpić do analizy wyników – wciskamy więc przycisk „calculate” i w okienku tekstowym pokazują się wyniki obliczeń. Trzeba tutaj wspomnieć iż dane pomiarowe są uśredniane do wartości godzinowych i następnie na podstawie pomiarowych zostaje wyznaczona prędkości wiatru na wysokości generatora elektrowni wiatrowej. Wyznaczona prędkość wiatru podstawiana jest do krzywej mocy z której to pobieramy generowaną moc. Tak oto sprawdzane są wszystkie zgromadzone dane pomiarowe.

Na zakończenie obliczeń dostępne jest podsumowanie zarówno generowanej energii elektrycznej jak i tzw. „full load hours” (czyli ile godzin dla danych pomiarowych elektrownia wiatrowa pracowałaby z pełną mocą).

Dla tak przygotowanych obliczeń użytkownik ma do wyboru:

    zapis wyników obliczeń do pliku txt (wyniki obliczeń które mogą zostać wykorzystane podczas dalszych analiz w excelu)
    podgląd szczegółowych wyników obliczeń w formie graficznej,
    podgląd podsumowania wyników obliczeń w formie graficznej
    zapis wyników obliczeń do pliku pdf (wyniki prezentowane w formie czytelnej dla użytkownika (patrz przykładowy raport)

Należy tutaj dodać obliczenia mogą być generowane dla kilku różnych elektrowni wiatrowych jak i elektrowni wiatrowych z inną wysokością wieży czy krzywą mocy.

Jeżeli masz pytania dotyczące tego programu proszę o kontakt.
