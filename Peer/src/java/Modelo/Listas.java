/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aplicacao.TelaLog;

/**
 *
 * @author Helbert Monteiro
 */
public class Listas {
    
    public static List<Arquivo>         LISTA_ARQUIVOS = new ArrayList<>();
    public static List<ArquivoDownload> LISTA_FILES    = new ArrayList<>();
    TelaLog telaLog = new TelaLog();

    private void verificarThreads(List<Thread> lista){
        List<Thread> listaThreads = lista;
        int indice = listaThreads.size();
        while(indice > 0){
            for(int i = 0; i < listaThreads.size(); i++){
                if(!listaThreads.get(i).isAlive()){
                    listaThreads.remove(listaThreads.get(i));
                    indice--;
                    telaLog.logArea.append("Threads abertas: " + indice);
                    System.out.println("Threads abertas: " + indice);
                }
            }
        }
        barra.setStringPainted(false);
        telaLog.logArea.append("download feito!\n");
        System.out.println("download feito!\n");
    }
    
}
