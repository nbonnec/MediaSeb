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

import android.os.Build;

import com.nbonnec.mediaseb.BaseTestCase;
import com.nbonnec.mediaseb.BuildConfig;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpointsImpl;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.models.MediaStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MSSInterpreterImplTest extends BaseTestCase {
    MSSEndpoints mssEndpoints;

    public MSSInterpreterImplTest() {
        this.mssEndpoints = new MSSEndpointsImpl();
    }

    @Test
    public void test_resultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("html/results.html"));
        List<Media> medias = mediaList.getMedias();

        assertThat(mediaList.getNextPageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/facettes/walking+dead+/ligne?limit=10&start=10");
        assertThat(medias.get(0).getTitle()).isEqualTo("Dead man walking : bande originale du film");
        assertThat(medias.get(0).getAuthor()).isEqualTo("Robbins, Tim");
        assertThat(medias.get(0).getEditor()).isEqualTo("Sony Music");
        assertThat(medias.get(0).getYear()).isEqualTo(("P 1995"));
        assertThat(medias.get(0).getCollection()).isEqualTo(DefaultFactory.Media.EMPTY_FIELD_COLLECTION);
        assertThat(medias.get(0).getImageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/index.php?option=com_opac&view=Ajax&task=couvertureAjax&tmpl=component&format=raw&num_ntc=520486958:103&is_media_ntc=0&type_ntc=6&taille=moyenne&support=ico_sup_03.png&lib_support=CD&isbn=&ean=&titre=Dead+man+walking&editeur=Sony+Music&auteur=Robbins%2C+Tim&show_lnk=118&Itemid=118");
        assertThat(medias.get(0).getNoticeUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/notice/520486958-103");

        assertThat(medias.get(2).getTitle()).isEqualTo("Walking dead. 12, un monde parfait");
        assertThat(medias.get(2).getAuthor()).isEqualTo("Kirkman, Robert");
        assertThat(medias.get(2).getEditor()).isEqualTo("Delcourt");
        assertThat(medias.get(2).getCollection()).isEqualTo("Contrebande");
        assertThat(medias.get(2).getImageUrl()).isEqualTo("http://ecx.images-amazon.com/images/I/61-5KGEFv9L.jpg");
        assertThat(medias.get(2).getNoticeUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/notice/134223724-103");
    }

    @Test
    public void test_firstResponseParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("html/first-response.html"));
        List<Media> medias = mediaList.getMedias();

        assertThat(mediaList.getNextPageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/facettes/walking+Dead+/ligne?limit=10&start=20");
        assertThat(medias.get(0).getTitle()).isEqualTo("Walking dead. 11, les chasseurs");
        assertThat(medias.get(0).getAuthor()).isEqualTo("Kirkman, Robert");
        assertThat(medias.get(0).getEditor()).isEqualTo("Delcourt");
        assertThat(medias.get(0).getYear()).isEqualTo(("2010"));
        assertThat(medias.get(0).getCollection()).isEqualTo("Contrebande");
        assertThat(medias.get(0).getImageUrl()).isEqualTo("http://ecx.images-amazon.com/images/I/51%2BaiNu8K3L.jpg");
        assertThat(medias.get(0).getLoadingImageUrl()).isNull();
        assertThat(medias.get(0).getNoticeUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/notice/134292892-103");

        assertThat(medias.get(1).getTitle()).isEqualTo("Walking dead. 01, passé décomposé");
        assertThat(medias.get(1).getAuthor()).isEqualTo("Kirkman, Robert");
        assertThat(medias.get(1).getEditor()).isEqualTo("Delcourt");
        assertThat(medias.get(1).getYear()).isEqualTo(("2007"));
        assertThat(medias.get(1).getCollection()).isEqualTo("Contrebande");
        assertThat(medias.get(1).getImageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/index.php?option=com_opac&view=Ajax&task=couvertureAjax&tmpl=component&format=raw&num_ntc=134300540:103&is_media_ntc=0&type_ntc=0&taille=moyenne&support=ico_sup_00.png&lib_support=&isbn=2756009121&ean=9782756009124&titre=Walking+dead.+01%2C+passe+decompose&editeur=Delcourt&auteur=Kirkman%2C+Robert&show_lnk=118&Itemid=118");
        assertThat(medias.get(1).getLoadingImageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/templates/c3rb_alpha_25/html/com_opac/assets/images/icones_support/ico_sup_00.png");
        assertThat(medias.get(1).getNoticeUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/notice/134300540-103");
    }

    @Test
    public void test_noResultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("html/no-results.html"));

        assertThat(mediaList.getMedias()).isEmpty();
        assertThat(mediaList.getNextPageUrl()).isEqualTo(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL);
    }

    @Test
    public void test_noticeParsing() throws IOException, ParseException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        Media media = interpreter.interpretNoticeFromHtml(readAssetFile("html/details-walking-dead.html"));

        assertThat(media.getSummary()).isEqualTo("Les amis en visite à Alexandria sont un renfort" +
                " appréciable dans l'affrontement contre les ennemis présents" +
                " dans les rangs des rôdeurs. Electre 2015.");
        assertThat(media.getType()).isEqualTo("Bd Adulte");
        assertThat(media.getSection()).isEqualTo("Espace adulte");
        assertThat(media.getLocation()).isEqualTo("BD");
        assertThat(media.getRating()).isEqualTo("");
        assertThat(media.getStatus()).isEqualTo(MediaStatus.LOANED);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        assertThat(media.getReturnDate()).isEqualTo(fmt.parse("25/03/2016"));

        media = interpreter.interpretNoticeFromHtml(readAssetFile("html/details-endymion.html"));
        assertThat(media.getSummary()).isEqualTo(DefaultFactory.Media.EMPTY_FIELD_SUMMARY);
        assertThat(media.getType()).isEqualTo("Livre");
        assertThat(media.getSection()).isEqualTo("Espace adulte");
        assertThat(media.getLocation()).isEqualTo("Réserve");
        assertThat(media.getRating()).isEqualTo("R SIM");
        assertThat(media.getStatus()).isEqualTo(MediaStatus.AVAILABLE);

        media = interpreter.interpretNoticeFromHtml(readAssetFile("html/details-bodyguard.html"));
        assertThat(media.getSummary()).isEqualTo("A 15 ans, Connor Reeves travaille pour l'agence" +
                " Bodyguard chargée de protéger les enfants de personnalités. Pour cette mission," +
                " il est le garde du corps de Ambre et Henri, les enfants de l'ambassadeur de France" +
                " au Burundi dont la famille est conviée à un safari par le président Bagaza." +
                " Mais un groupe de rebelles attaque le groupe, Connor parvient à fuir dans la" +
                " brousse avec les deux enfants.");
        assertThat(media.getType()).isEqualTo("Livre");
        assertThat(media.getSection()).isEqualTo("Espace jeunesse");
        assertThat(media.getLocation()).isEqualTo("Fiction jeunesse");
        assertThat(media.getRating()).isEqualTo("BRA");
        assertThat(media.getStatus()).isEqualTo(MediaStatus.RESERVED);
    }

    @Test
    public void test_ImageUrlParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        String imageUrl = interpreter.interpretImageUrlFromHtml(readAssetFile("html/cover-return.html"));

        assertThat(imageUrl).isEqualTo("http://mediatheque.saintsebastien.fr/cache/9782756039572_grande.jpg");
    }

    @Test
    public void test_LoanParsing() throws IOException, ParseException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        List<Media> medias = interpreter.interpretLoansFromHtml(readAssetFile("html/loans.html"));

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

        assertThat(medias.get(0).getTitle()).isEqualTo("Hyperion. 03, Endymion");
        assertThat(medias.get(0).getAuthor()).isEqualTo("Simmons, Dan");
        assertThat(medias.get(0).getReturnDate()).isEqualTo(fmt.parse("16/08/2016"));
        assertThat(medias.get(0).getLoanDate()).isEqualTo(fmt.parse("31/05/2016"));

        assertThat(medias.get(4).getTitle()).isEqualTo("Walking dead. 23, murmures");
        assertThat(medias.get(4).getAuthor()).isEqualTo("Kirkman, Robert");
        assertThat(medias.get(4).getReturnDate()).isEqualTo(fmt.parse("01/08/2016"));
        assertThat(medias.get(4).getLoanDate()).isEqualTo(fmt.parse("31/05/2016"));
    }

    @Test
    public void test_InputsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        Hashtable<String, String> table = interpreter.interpretTokenFromHtml(readAssetFile("html/login-attempt.html"));
        Hashtable<String, String> testTable = new Hashtable<>();

        testTable.put("option", "com_opac");
        testTable.put("task", "login");
        testTable.put("return", "aW5kZXgucGhwP0l0ZW1pZD0xMTQ=");
        testTable.put("Itemid", "0");
        testTable.put("mod_id", "102");
        testTable.put("30c714561c0e699462bd5de36d90219a", "1");

        assertThat(table).isEqualTo(testTable);
    }

    @Test
    public void test_LoginParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        boolean login = interpreter.interpretLoginFromHtml(readAssetFile("html/login-fail.html"));

        assertThat(login).isFalse();
    }

    @Test
    public void test_AccountParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        String date = interpreter.interpretAccountFromHtml(readAssetFile("html/account.html"));

        assertThat(date).isEqualTo("24/08/2016");
    }
}
