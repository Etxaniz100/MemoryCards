package com.example.memorycards;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper
{
    public BaseDatos(@Nullable Context context, @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);


    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE Mazo ('Nombre' VARCHAR(255) PRIMARY KEY NOT NULL)");

        db.execSQL("CREATE TABLE Huevo ('Nombre' VARCHAR(255) PRIMARY KEY NOT NULL, 'Progreso' REAL, 'Felicidad' REAL, 'UltimaVezAbierto' DATE, 'Color' VARCHAR(255))");

        db.execSQL( "CREATE TABLE Carta ('Pregunta' VARCHAR(500) NOT NULL, " +
                                        "'NombreMazo' VARCHAR(255) NOT NULL, " +
                                        "'Respuesta' VARCHAR(1000) NOT NULL,"+
                                        "'Estado' INTEGER,"+                     // Se ha estudiado la carta?
                                        "'ProximoEstudio' DATE,"+               // Cuando esta programado que se vuelva a estudiar la carta
                                        "'DiasEntreEstudio' INTEGER,"+
                                        "'UnaVezCorrecto' INTEGER ,"+
                                        "PRIMARY KEY('Pregunta', 'NombreMazo'))");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
