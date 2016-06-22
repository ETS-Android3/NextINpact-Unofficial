/*
 * Copyright 2016 Anael Mobilia
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
package com.pcinpact;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;

import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import java.util.ArrayList;

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Accès à la base de données
     */
    DAO monDAO;
    /**
     * les articles
     */
    ArrayList<ArticleItem> mesArticles;

    /**
     * Variables passées à ArticleFragment
     */
    private Context monContext;
    private LayoutInflater monLayoutInflater;

    public ArticlePagerAdapter(FragmentManager fm, Context unContext, LayoutInflater unLayoutInflater) {
        super(fm);

        monContext = unContext;
        monLayoutInflater = unLayoutInflater;
        monDAO = DAO.getInstance(monContext);

        // Nombre d'articles à afficher
        int maLimite = Constantes.getOptionInt(monContext, R.string.idOptionNbArticles, R.string.defautOptionNbArticles);
        mesArticles = monDAO.chargerArticlesTriParDate(maLimite);
    }

    @Override
    public Fragment getItem(int position) {
        // Récupération de la position de l'article
        int articleID = getArticleID(position);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ArticlePagerAdapter", "getItem() - " + position + " => #" + articleID);
        }

        return new ArticleFragment(monContext, monLayoutInflater, articleID);
    }

    @Override
    public int getCount() {
        int nbArticles = mesArticles.size();
        return nbArticles;
    }

    /**
     * ID de l'article à partir de sa position
     *
     * @param position position dans la liste
     * @return ID de l'article
     */
    public int getArticleID(int position) {
        return mesArticles.get(position).getId();
    }

    /**
     * Fourni la position d'un article à partir de son ID
     *
     * @param articleID ID de l'article
     * @return position affichée de l'article
     */
    public int getPosition(int articleID) {
        int index = 0;
        boolean result = false;

        // Parcours de l'ensemble à la recherche de mon articleID
        while (index < mesArticles.size()) {
            if (mesArticles.get(index).getId() == articleID) {
                return index;
            }
            index++;
        }
        return 0;
    }
}

