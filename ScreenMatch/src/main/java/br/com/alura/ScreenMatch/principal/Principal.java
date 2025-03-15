package br.com.alura.ScreenMatch.principal;

import br.com.alura.ScreenMatch.model.DadosEpisodio;
import br.com.alura.ScreenMatch.model.DadosSerie;
import br.com.alura.ScreenMatch.model.DadosTemporada;
import br.com.alura.ScreenMatch.model.Episodio;
import br.com.alura.ScreenMatch.service.ConsumoApi;
import br.com.alura.ScreenMatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=142777fe";

    public void exibeMenu() {

        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine().replace(" ", "+");
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);
        System.out.println(json);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> lstTemporadas = new ArrayList<DadosTemporada>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            lstTemporadas.add(dadosTemporada);
        }
        lstTemporadas.forEach(System.out::println);

        //for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
        //    List<DadosEpisodio> episodiosTemporada = lstTemporadas.get(i).episodios();
        //    for (int j = 0; j < episodiosTemporada.size(); j++) {
        //        System.out.println("EP" + episodiosTemporada.get(j).numeroEpisodio() + " " + episodiosTemporada.get(j).titulo());
        //    }
        //}

        //lstTemporadas.forEach(t -> t.episodios().forEach(ep -> System.out.println("EP" + ep.numeroEpisodio() + " " + ep.titulo())));

//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n -> n.startsWith("N"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);
        List<DadosEpisodio> dadosEpisodios = lstTemporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        dadosEpisodios.stream()
//                .filter(ep -> !ep.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação " + e))
//                .limit(5)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);
//                .forEach(ep -> System.out.println("EP" + ep.numeroEpisodio() + "-" + ep.titulo() + "| " + ep));

        List<Episodio> episodios = lstTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

//        episodios.forEach(System.out::println);
//
//        System.out.println("Digite o trecho do titulo do episódio: ");
//        String trechoTitulo = leitura.nextLine();
//
//        Optional<Episodio> episodio = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (episodio.isPresent()) {
//            System.out.println(episodio);
//            System.out.println("Temporada: " + episodio.get().getNumeroTemporada());
//        }
//        else {
//            System.out.println("Episódio não encontrado!");
//        }
//
//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getNumeroTemporada() + ", " +
//                        "Episódio: " + e.getNumeroEpisodio() + ", " +
//                        "Título: " + e.getTitulo() + ", " +
//                        "Data Lançamento: " + e.getDataLancamento().format(formatador)
//                ));
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0)
                .collect(Collectors.groupingBy(
                            Episodio::getNumeroTemporada,
                            Collectors.averagingDouble(Episodio::getAvaliacao)
                ));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}