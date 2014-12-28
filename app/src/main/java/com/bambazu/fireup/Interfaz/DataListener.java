package com.bambazu.fireup.Interfaz;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by ubiquity on 9/22/14.
 */
public interface DataListener {
    void retrieveData(List<ParseObject> data);
}
