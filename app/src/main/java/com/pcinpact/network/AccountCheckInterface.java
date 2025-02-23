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

/**
 * Interface générique de callback à la fin de vérification d'identifiants
 *
 * @author Anael
 */
public interface AccountCheckInterface {

    /**
     * Retour d'une vérification d'identifiants abonnés
     *
     * @param token Token à utiliser pour se connecter chez NXI (vide si pas auth)
     */
    void retourVerifCompte(final String token);
}