package com.example.steve.impromptu.Entity;

import java.util.List;

/**
 * Created by Steve on 12/7/2014.
 */
public abstract class UpdateView {
    public UpdateView() {}
    public abstract void update(List<Event> events);
}
