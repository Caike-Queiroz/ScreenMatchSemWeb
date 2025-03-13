package br.com.alura.ScreenMatch.principal;

import br.com.alura.ScreenMatch.model.DadosEpisodio;
import br.com.alura.ScreenMatch.model.DadosSerie;
import br.com.alura.ScreenMatch.model.DadosTemporada;
import br.com.alura.ScreenMatch.service.ConsumoApi;
import br.com.alura.ScreenMatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            DadosTemporada dadosTemporada1 = conversor.obterDados(json, DadosTemporada.class);
            lstTemporadas.add(dadosTemporada1);
        }
        lstTemporadas.forEach(System.out::println);

        //for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
        //    List<DadosEpisodio> episodiosTemporada = lstTemporadas.get(i).episodios();
        //    for (int j = 0; j < episodiosTemporada.size(); j++) {
        //        System.out.println("EP" + episodiosTemporada.get(j).numeroEpisodio() + " " + episodiosTemporada.get(j).titulo());
        //    }
        //}

        lstTemporadas.forEach(t -> t.episodios().forEach(ep -> System.out.println("EP" + ep.numeroEpisodio() + " " + ep.titulo())));
        //lstTemporadas.forEach(System.out::println);
    }
}