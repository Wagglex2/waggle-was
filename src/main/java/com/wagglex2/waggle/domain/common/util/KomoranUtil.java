package com.wagglex2.waggle.domain.common.util;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KomoranUtil {

    private static final String USER_DIC_PATH = "src/main/resources/komoran/dic.user";
    private static final Komoran komoran = createKomoran();

    private static Komoran createKomoran() {
        Komoran k = new Komoran(DEFAULT_MODEL.LIGHT);
        k.setUserDic(USER_DIC_PATH);

        return k;
    }

    public List<String> getNouns(String target) {
        return komoran.analyze(target).getNouns();
    }
}
