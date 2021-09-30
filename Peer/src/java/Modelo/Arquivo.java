package Modelo;

import Controle.TorrentFilesManage;
import aplicacao.TelaLista;
import aplicacao.TelaLog;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Helbert Monteiro
 */
public class Arquivo {
    
    private String           nome;
    private double           tamanhoArquivo;
    private int              tamanhoVetor;
    private String           hashArquivo;
    private List<String>     peers;
    TelaLog telaLog = new TelaLog();
    
    public Arquivo(File file) throws IOException, NoSuchAlgorithmException{
        this.nome           = file.getName();
        this.tamanhoArquivo = file.length() / 1024 + 1;
        this.tamanhoVetor   = (int) file.length();
        this.hashArquivo    = new TorrentFilesManage().getHashCode(new TorrentFilesManage().createFullArrayFromFile(file));
    }
    
    public Arquivo(){}
    
    public String getNome(){
        return nome;
    }
    
    public String getHashArquivo() {
        return hashArquivo;
    }

    public double getTamanhoArquivo() {
        return tamanhoArquivo;
    }
    
    public List<String> getPeer(){
        return peers;
    }
    
    public int getTamanhoVetor(){
        return tamanhoVetor;
    }
    public void salvarArquivo(Arquivo arquivo, int[] v1, byte[] v2){

        for(int i = 0; i < v1.length; i++){
            v2[i] = (byte) v1[i];
        }
        telaLog.logArea.append("verificando...");
        System.out.println("verificando...");
        try {
            if(new TorrentFilesManage().getHashCode(v2).equals(arquivo.getHashArquivo())){
                new TorrentFilesManage().createFileFromByteArray("C://ThorEnt//" + arquivo.getNome(), v2);
                System.out.println("ok");
                System.out.println("salvo!");
            }else{
                telaLog.logArea.append("Hash incorreto");
                telaLog.logArea.append("Hash esperado: " + arquivo.getHashArquivo());
                telaLog.logArea.append("Hash do arquivo baixado: " + new TorrentFilesManage().getHashCode(v2));
                System.out.println("Hash incorreto");
                System.out.println("Hash esperado: " + arquivo.getHashArquivo());
                System.out.println("Hash do arquivo baixado: " + new TorrentFilesManage().getHashCode(v2));
            }
            //new TorrentFilesManage().createFileFromByteArray("C://ThorEnt//testando.jpg", vetor_final);
        } catch (Exception ex) {
            telaLog.logArea.append("Salvar arquivo: " + ex.getMessage());
            System.out.println("Salvar arquivo: " + ex.getMessage());
            Logger.getLogger(TelaLista.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Arquivo> prepararServidor(Arquivo arquivo, int[] v1, int valor){
        int    tamanho   = valor;//arquivo.getTamanhoVetor();
        int    numero_peers    = arquivo.getPeer().size();
        int    l               = 0;
        int[]  vetor_p1 = v1;
        int    tamanho_bloco   = (int) tamanho / (numero_peers * 5);
        
        int progress = 0;
        barra.setMinimum(0);
        barra.setMaximum(tamanho-1);
        barra.setValue(0);
        barra.setStringPainted(true);
        
        List<Thread> listaThreads = new ArrayList<>();
        
        List<PeerModelo> peers = new ArrayList<>();
        PeerModelo       peer;
        for(int i = 0; i < arquivo.getPeer().size(); i ++){
            peer = new PeerModelo();
            peer.setIp(arquivo.getPeer().get(i));
            peer.setDisponibilidade(true);
            peers.add(peer);
        }
        for(int i = 0; i < vetor_p1.length; i++){
            vetor_p1[i] = 200;
        }

        telaLog.logArea.append("fazendo download...\n");
        telaLog.logArea.append("Arquivo: " + arquivo.getNome());
        telaLog.logArea.append("Tamanho: " + arquivo.getTamanhoArquivo());
        System.out.println("fazendo download...\n");
        System.out.println("Arquivo: " + arquivo.getNome());
        System.out.println("Tamanho: " + arquivo.getTamanhoArquivo());
        for(int i = 0; i < vetor_p1.length; i++){
            if(vetor_p1[i] < -128 || vetor_p1[i] > 127){
                for(int j = 0; j < numero_peers; j++){
                    if(peers.get(j).getDisponibilidade()){
                        int ii = i;
                        i += tamanho_bloco;
                        int jj = j;
                        String hashArquivo = arquivo.getHashArquivo();
                        String nome        = arquivo.getNome();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                peers.get(jj).setDisponibilidade(false);
                                int inicio_bloco = ii;
                                telaLog.logArea.append("Peer escolhido " + peers.get(jj).getIp() + ": pacote " + inicio_bloco);
                                System.out.println("Peer escolhido " + peers.get(jj).getIp() + ": pacote " + inicio_bloco);
                                ArquivoDownload arquivoDownload = new ArquivoDownload();
                                String url = "http://"+peers.get(jj).getIp()+":8080/Peer/webresources/peer/download/"+tamanho_bloco+"/"+inicio_bloco+"/"+hashArquivo;
                                try{
                                    String jsonDownload = new Conexao().conectaWebService(url, null, "GET");
                                    if(!jsonDownload.equals(null)){
                                        arquivoDownload = new Gson().fromJson(jsonDownload, ArquivoDownload.class);
                                        peers.get(jj).setDisponibilidade(true);

                                        byte[] vetor_menor = new byte[arquivoDownload.getVetor().length];
                                        vetor_menor = arquivoDownload.getVetor();
                                        String hash = new TorrentFilesManage().getHashCode(vetor_menor);
                                        if(hash.equals(arquivoDownload.getHash())){
                                            telaLog.logArea.append("hash vetor ok: pacote " + inicio_bloco);
                                            System.out.println("hash vetor ok: pacote " + inicio_bloco);
                                            for(int k = 0; k < vetor_menor.length; k++){
                                                if(inicio_bloco < tamanho){
                                                    vetor_p1[inicio_bloco] = vetor_menor[k];
                                                    inicio_bloco++;
                                                }
                                            }
                                            //i += vetor_menor.length;
                                        }else{
                                            System.out.println("not");
                                            for(int k = 0; k < vetor_menor.length; k++){
                                                if(inicio_bloco < tamanho){
                                                    vetor_p1[inicio_bloco] = -200;
                                                    inicio_bloco++;
                                                }
                                            }
                                            //i = inicio_bloco;
                                        }
                                    }else{
                                        peers.get(jj).setDisponibilidade(false);
                                    }
                                }catch(JsonSyntaxException | NoSuchAlgorithmException erro){
                                    System.out.println("Erro na thread: " + erro.getMessage());
                                }
                            }
                        });
                        listaThreads.add(thread);
                        thread.start();
                    }else{
                        l = j;
                        while(!peers.get(l).getDisponibilidade()){
                            //System.out.println("peer off " + peers.get(l).getIp());
                            l++;
                            if(l >= peers.size()){
                                //System.out.println("recomeça lista de peers...");
                                l = 0;
                            }
                        }
                        j = l - 1;
                    }
                }
                i--;
            }
            progress++;
            barra.setStringPainted(true);
            barra.setValue(progress);
        }
    }
}