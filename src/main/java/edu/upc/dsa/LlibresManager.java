package edu.upc.dsa;

import edu.upc.dsa.exceptions.TrackNotFoundException;

import java.util.List;

public interface TracksManager {


    public Track addTrack(String id, String title, String singer);
    public Track addTrack(String title, String singer);
    public Track addTrack(Track t);
    public Track getTrack(String id);
    public Track getTrack2(String id) throws TrackNotFoundException;

    public List<Track> findAll();
    public void deleteTrack(String id);
    public Track updateTrack(Track t);

    public void clear();
    public int size();
}
