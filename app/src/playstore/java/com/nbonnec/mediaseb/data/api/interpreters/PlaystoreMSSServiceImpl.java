/*
 * Copyright 2016 nbonnec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nbonnec.mediaseb.data.api.interpreters;

import android.accounts.NetworkErrorException;

import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Account;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.models.MediaStatus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class PlaystoreMSSServiceImpl implements MSSService {

    public static final String TAG = PlaystoreMSSServiceImpl.class.getSimpleName();

    public PlaystoreMSSServiceImpl() {
    }

    @Override
    public Observable<MediaList> getMediaList(String url) {
        Timber.d("Displaying free medias.");
        return Observable.just(getFreeMedias());
    }

    @Override
    public Observable<Media> getMediaDetails(String url) {
        return Observable.error(new NetworkErrorException(TAG +
                ": getMediaDetails - Simulated Bad Network Request"));
    }

    @Override
    public Observable<String> getMediaLoadedImageUrl(String url) {
        return Observable.error(new NetworkErrorException(TAG +
                ": getMediaLoadedImageUrl - Simulated Bad Network Request"));
    }

    @Override
    public Observable<MediaList> getResults(String pattern) {
        return Observable.error(new NetworkErrorException(TAG +
                ": getResults - Simulated Bad Network Request"));
    }

    @Override
    public Observable<MediaList> getNews() {
        return Observable.error(new NetworkErrorException(TAG +
                ": getNews - Simulated Bad Network Request"));
    }

    @Override
    public Observable<Boolean> login(String name, String cardNumber) {
        return Observable.error(new NetworkErrorException(TAG +
                ": login - Simulated Bad Network Request"));
    }

    @Override
    public Observable<Account> getAccountDetails() {
        return Observable.error(new NetworkErrorException(TAG +
                ": getAccountDetails - Simulated Bad Network Request"));
    }

    @Override
    public Observable<String> getHtml(String name) {
        return Observable.error(new NetworkErrorException(TAG +
                ": getHtml - Simulated Bad Network Request"));
    }

    /**
     * Create a list of free medias
     * @return Medialist of free medias.
     */
    private MediaList getFreeMedias() {
        MediaList mediaList = DefaultFactory.MediaList.constructDefaultInstance();
        Media media = DefaultFactory.Media.constructDefaultInstance();
        List<Media> medias = new ArrayList<>();

        media.setTitle("Du côté de chez Swann");
        media.setAuthor("Marcel Proust");
        media.setYear("1913");
        media.setSummary("Du côté de chez Swann est un roman de Marcel Proust, c'est le premier volume de À la recherche du temps perdu. Il est composé de trois parties, dont les titres sont : Combray, Un amour de Swann et Nom de pays : le nom.");
        media.setImageUrl("file:///android_asset/du-cote-de-chez-swan.jpg");
        media.setType("Livre");
        media.setStatus(MediaStatus.AVAILABLE);
        medias.add(media);

        media = DefaultFactory.Media.constructDefaultInstance();
        media.setTitle("Discours de la méthode");
        media.setAuthor("René Descartes");
        media.setYear("1637");
        media.setSummary("Le Discours de la méthode, publié en 1637, est le premier texte philosophique écrit par René Descartes et le premier ouvrage qui traite du sujet en langue française (par opposition à la tradition scientifique de l'époque de rédiger en latin).");
        media.setImageUrl("file:///android_asset/le-discours-de-la-methode.jpg");
        media.setType("Livre");
        medias.add(media);

        media = DefaultFactory.Media.constructDefaultInstance();
        media.setTitle("Big Buck Bunny");
        media.setAuthor("Sacha Goedegebure");
        media.setYear("2008");
        media.setSummary("Dans un monde coloré, tout va pour le mieux : un gros lapin se réveille et sort de sa tanière. Il respire à pleins poumons les essences du printemps et admire les papillons. Seulement, c'est sans compter la méchanceté de trois rongeurs (Frank, Rinky et Gamera) qui tuent plusieurs de ces papillons sous les yeux abasourdis du lapin. Celui-ci décide alors de se venger. Après une longue préparation de divers pièges, les trois mammifères vont respectivement se faire faucher par un tronc en balancement, se faire catapulter et finir en cerf-volant.");
        media.setImageUrl("file:///android_asset/big-buck-bunny.jpg");
        media.setType("DVD");
        medias.add(media);

        media = DefaultFactory.Media.constructDefaultInstance();
        media.setTitle("Sintel");
        media.setAuthor("Colin Levy");
        media.setYear("2010");
        media.setSummary("Une jeune femme solitaire, Sintel, secourt et se lie d'amitié avec un dragonneau, qu'elle nomme Scales. Mais lorsque celui-ci se fait enlever par un dragon adulte, Sintel décide de se lancer dans une dangereuse quête pour retrouver son compagnon.");
        media.setImageUrl("file:///android_asset/sintel.jpg");
        media.setType("DVD");
        medias.add(media);

        media = DefaultFactory.Media.constructDefaultInstance();
        media.setTitle("Voyage au centre de la Terre");
        media.setAuthor("Jules Verne");
        media.setYear("1864");
        media.setSummary("Le professeur Lidenbrock trouve un document dans lequel il apprend l'existence d'un volcan éteint dont la cheminée pourrait le conduire jusqu'au centre de la Terre. Accompagné de son neveu Axel et du guide Hans, il se rend au volcan Sneffels, en Islande, et s'engouffre dans les entrailles de la Terre. Ils ne tarderont pas à faire d'étonnantes découvertes...\n");
        media.setImageUrl("file:///android_asset/voyage-au-centre-de-la-terre.jpg");
        media.setType("Livre");
        medias.add(media);

        mediaList.setMedias(medias);

        return mediaList;
    }
}
