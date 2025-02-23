/*
 * Copyright 2013 - 2022 Anael Mobilia and contributors
 *
 * This file is part of NextINpact-Unofficial.
 *
 * NextINpact-Unofficial is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NextINpact-Unofficial is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NextINpact-Unofficial. If not, see <http://www.gnu.org/licenses/>
 */
package com.pcinpact.network;

import android.os.AsyncTask;
import android.util.Log;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.parseur.ParseurHTML;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyURLUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

/**
 * téléchargement du code HTML.
 *
 * @author Anael
 */
public class AsyncHTMLDownloader extends AsyncTask<String, Void, ArrayList<? extends Item>> {
    /**
     * Parent qui sera rappelé à la fin.
     */
    private final WeakReference<RefreshDisplayInterface> monParent;
    /**
     * Site concerné (IH, NXI, ...).
     */
    private final int site;
    /**
     * Paramètres de l'URL de base du site.
     */
    private final String pathURL;
    /**
     * URL FQDN.
     */
    private final String fullURL;
    /**
     * Type de la ressource.
     */
    private final int typeHTML;
    /**
     * PK de l'article lié (DL article ou commentaires)
     */
    private final int pkArticle;
    /**
     * Token du compte NXI/IH
     */
    private final String token;

    /**
     * Téléchargement d'une ressource
     *
     * @param parent       parent à callback à la fin
     * @param unType       type de la ressource (Cf Constantes.TYPE_)
     * @param unSite       ID du site (NXI, IH, ...)
     * @param unPathURL    Chemin à ajouter à l'URL
     * @param unePkArticle PK de l'article (cas DL article & commentaires)
     * @param unToken      token de connexion
     */
    public AsyncHTMLDownloader(final RefreshDisplayInterface parent, final int unType, final int unSite, final String unPathURL,
                               final int unePkArticle, final String unToken) {
        // Mappage des attributs de cette requête
        // On peut se permettre de perdre le parent
        monParent = new WeakReference<>(parent);
        site = unSite;
        pathURL = unPathURL;
        fullURL = MyURLUtils.getSiteURL(unSite, unPathURL, false);
        typeHTML = unType;
        pkArticle = unePkArticle;
        token = unToken;
    }

    @Override
    protected ArrayList<? extends Item> doInBackground(String... params) {
        ArrayList<? extends Item> monRetour = new ArrayList<>();

        // Récupération du contenu HTML
        String datas = Downloader.download(fullURL, token);

        // Vérifie que j'ai bien un retour (vs erreur DL)
        if (datas != null) {
            switch (typeHTML) {
                case Constantes.HTML_LISTE_ARTICLES:
                    monRetour = ParseurHTML.getListeArticles(site, datas);
                    break;

                case Constantes.HTML_ARTICLE:
                    ArticleItem monArticle = new ArticleItem();
                    monArticle.setPk(pkArticle);
                    // Récupération des infos du contenu
                    ArticleItem articleParse = ParseurHTML.getContenuArticle(datas, site);
                    monArticle.setContenu(articleParse.getContenu());
                    // URL SEO
                    monArticle.setURLseo(articleParse.getURLseo());

                    ArrayList<ArticleItem> monRetourTmp = new ArrayList<>();
                    monRetourTmp.add(monArticle);
                    monRetour = monRetourTmp;
                    break;

                case Constantes.HTML_COMMENTAIRES:
                    monRetour = ParseurHTML.getCommentaires(datas, pkArticle);
                    break;

                case Constantes.HTML_NOMBRE_COMMENTAIRES:
                    monRetour = ParseurHTML.getNbCommentaires(datas, site);
                    break;

                default:
                    if (Constantes.DEBUG) {
                        Log.e("AsyncHTMLDownloader",
                              "doInBackground() - type HTML incohérent : " + typeHTML + " - URL : " + fullURL);
                    }
                    break;
            }
        } else {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("AsyncHTMLDownloader", "doInBackground() - pas de contenu retourné pour " + fullURL);
            }
        }
        return monRetour;
    }

    @Override
    protected void onPostExecute(ArrayList<? extends Item> result) {
        try {
            // Le parent peut avoir été garbage collecté
            monParent.get().downloadHTMLFini(site, pathURL, result);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "onPostExecute()", e);
            }
        }
    }

    /**
     * Lancement du téléchargement asynchrone
     *
     * @return résultat de la commande
     */
    public boolean run() {
        boolean monRetour = true;

        try {
            // Parallélisation des téléchargements pour l'ensemble de l'application
            this.execute();
        } catch (RejectedExecutionException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("AsyncHTMLDownloader", "run() - RejectedExecutionException (trop de monde en queue)", e);
            }

            // Je note l'erreur
            monRetour = false;
        }

        return monRetour;
    }
}