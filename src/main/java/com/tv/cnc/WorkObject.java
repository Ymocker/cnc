/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.cnc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.*;
import java.time.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;

/**
 *
 * @author VVV
 */
public class WorkObject {

    File file;
    double height, angle, length;
    boolean ramp;


    private double endLength;
    private double prevX = 0.0;
    private double prevY = 0.0;

    private double lowZ; // a half of difference between ramp start and ramp end
    private BufferedReader reader;
    private BufferedWriter writer;

public WorkObject(File file) {
    this.file = file;
    //var height is half of the start height

}

public void makeOut() throws IOException {
    String workS, s;
    double slowF, getXY;

    endLength = height/tan(toRadians(angle));

    //создаем BufferedReader для построчного считывания
    reader = new BufferedReader(new FileReader(file));
    writer = new BufferedWriter(new FileWriter("out-out1.tap"));

Instant start = Instant.now();

    // read the first line
    s = reader.readLine();

    while (s != null) {
        getXY = (getValue(s, 'X'));
        if (getXY != 100000000) {prevX = getXY;}
        getXY = (getValue(s, 'Y'));
        if (getXY != 100000000) {prevY = getXY;}

        if ((s.startsWith("G")) && (((s.startsWith("G1Z")) || (s.startsWith("G01Z")) || (s.startsWith("G1 Z")) || (s.startsWith("G01 Z")))))
        {
            lowZ = getValue(s, 'Z') + height;
            slowF = getValue(s, 'F');
            workS = String.format(Locale.ENGLISH, "Z%.3f", lowZ + height);
            writer.write(workS + System.lineSeparator());

            if (ramp) {
                workS = "G1 " + String.format(Locale.ENGLISH, "F%.1f", slowF);
                writer.write(workS + System.lineSeparator());

                s = makeRamp();
                //s = reader.readLine();
            } // if ramp
        } // start G
        writer.write(s + System.lineSeparator());
        s = reader.readLine();
    } // while

    writer.close();
    reader.close();

Instant finish = Instant.now();
long elapsed = Duration.between(start, finish).toMillis();
System.out.println("Прошло времени, мс: " + elapsed);

}

private double getValue (String str, char valName) {
    int vPos = str.indexOf(valName);
    if (vPos == -1) {
        return 100000000;
    }
    String outStr = str.substring(vPos+1);
    char[] arrCh = outStr.toCharArray();
    outStr = "";

    for (char Ch: arrCh) {
        if (Character.isDigit(Ch) || (Ch == '.') ||(Ch == '-')) {
            outStr += Ch;
        } else {
            return Double.parseDouble(outStr);
        }
    }

    return Double.parseDouble(outStr);
}

private String makeRamp() throws IOException {
    double dx = 0.1;
    double dy = 0.1;
    double dz = 0.1;
    double rate, newZ;
    double[] point = new double[4];
    String r, workS;
    boolean ex = true;

    point[0] = prevX;
    point[1] = prevY;
    point[2] = lowZ + height;
    point[3] = 0;  // delta Z

    ArrayList<double[]> out = new ArrayList<>();
    out.add(point);

    reader.mark(1000);

    while (ex) {
        point = new double[4];
        r = reader.readLine();

        if ((r.startsWith("G0Z")) || (r.startsWith("G0 Z")) || (r.startsWith("G00Z")) || (r.startsWith("G00 Z")))
        {
            ex = false;
            while (out.get(out.size() - 1)[2] > lowZ) {
                out = shortG(out);
            }
        } else {
            point[0] = (getValue(r, 'X') == 100000000) ? out.get(out.size() - 1)[0] : getValue(r, 'X');
            point[1] = (getValue(r, 'Y') == 100000000) ? out.get(out.size() - 1)[1] : getValue(r, 'Y');
            dx = point[0] - out.get(out.size() - 1)[0];
            dy = point[1] - out.get(out.size() - 1)[1];
            rate = sqrt(pow(dx, 2) + pow(dy, 2))/endLength; // part of full XY-move relatively endLength
            dz = height * rate; // part of full Z-move proportionally to part of X-Move relatively endLength
            point[2] = out.get(out.size() - 1)[2] - dz;
            point[3] = dz;

            if (point[2] < lowZ) {
                ex = false;
                point[2] = lowZ;
                point[3] = out.get(out.size() - 1)[2] - lowZ;
                rate = abs(point[3]/dz);
                dx *= rate;
                dy *= rate;
                point[0] = out.get(out.size() - 1)[0] + dx;
                point[1] = out.get(out.size() - 1)[1] + dy;
            }

            workS = String.format(Locale.ENGLISH, "X%.3f", point[0]);
            workS += String.format(Locale.ENGLISH, " Y%.3f", point[1]);
            workS += String.format(Locale.ENGLISH, " Z%.3f", point[2]);
            writer.write(workS + System.lineSeparator());

            out.add(point);
        }
    }

    ListIterator<double[]> itr = out.listIterator(out.size());
    //point = new double[4];
    point = itr.previous();
    dz = point[3];
    newZ = point[2];

    while(itr.hasPrevious()) {
        point = itr.previous();
        newZ -= dz;
        if (newZ < lowZ - height) {
            newZ = lowZ - height;
        }

        workS = String.format(Locale.ENGLISH, "X%.3f", point[0]);
        workS += String.format(Locale.ENGLISH, " Y%.3f", point[1]);
        workS += String.format(Locale.ENGLISH, " Z%.3f", newZ);
        writer.write(workS + System.lineSeparator());

        dz = point[3];
    }
    reader.reset();
    return reader.readLine();
}

private ArrayList shortG(ArrayList arr) throws IOException {
    double[] point = new double[4];
    double dz, nextDz, newZ;
    boolean ex = true;
    String workS;

    ArrayList<double[]> outclone = (ArrayList)arr.clone();
    //outclone = (ArrayList)arr.clone();

    ListIterator<double[]> itr = outclone.listIterator(outclone.size());
    point = itr.previous();
    dz = point[3];
    newZ = point[2];

    while(itr.hasPrevious() && ex) {
        point = new double[4];
        point = itr.previous();
        nextDz = point[3];
        newZ -= dz;
        point[2] = newZ;
        point[3] = dz;

        if (newZ < lowZ) {
            ex = false;
            point[2] = lowZ;
            point[3] = newZ + point[3] - lowZ;
        }
        dz = nextDz;

        workS = String.format(Locale.ENGLISH, "X%.3f", point[0]);
        workS += String.format(Locale.ENGLISH, " Y%.3f", point[1]);
        workS += String.format(Locale.ENGLISH, " Z%.3f", point[2]);
        writer.write(workS + System.lineSeparator());

        arr.add(point);
    }
    return arr;
}



//////////////////////
} // End of class

class Point {
    double x, y, z;
    Point(double x) {

    }
}
