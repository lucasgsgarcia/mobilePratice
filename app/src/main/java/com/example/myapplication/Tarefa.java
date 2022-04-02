package com.example.myapplication;

import java.util.Date;
import java.util.Objects;

public class Tarefa {
    private String descricao;
    private int duracao;
    private boolean feita;
    private Date quando;

    public Tarefa(String descricao, Date quando,
                  int duracao, boolean feita) {
        this.descricao = descricao;
        this.duracao = duracao;
        this.quando = quando;
        this.feita = feita;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public boolean isFeita() {
        return feita;
    }

    public void setFeita(boolean feita) {
        this.feita = feita;
    }

    public Date getQuando() {
        return quando;
    }

    public void setQuando(Date quando) {
        this.quando = quando;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarefa tarefa = (Tarefa) o;
        return duracao == tarefa.duracao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duracao);
    }

    public String toString() {
        return descricao + " - " + duracao + " - " +
                (feita ? " OK " : "--");
    }
}
