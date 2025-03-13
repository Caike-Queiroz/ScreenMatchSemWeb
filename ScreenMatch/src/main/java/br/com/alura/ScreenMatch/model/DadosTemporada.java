package br.com.alura.ScreenMatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTemporada(@JsonAlias("Title") String titulo,
                             @JsonAlias("Season") Integer numero,
                             @JsonAlias("totalSeasons") Integer totalTemporadas,
                             @JsonAlias("Episodes") List<DadosEpisodio> episodios) {}