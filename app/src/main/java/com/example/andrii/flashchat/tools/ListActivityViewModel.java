package com.example.andrii.flashchat.tools;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.andrii.flashchat.data.Model.Person;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListActivityViewModel extends ViewModel{

    private MutableLiveData<List<Person>> liveData = new MutableLiveData<>();

    public MutableLiveData<List<Person>> getData() {
        return liveData;
    }

    public void setData(List<Person> personList) {
        if (personList == null) return;

        List<Person> dataList = liveData.getValue();
        if (dataList == null && listEquals(dataList,personList)) return;
        else liveData.setValue(personList);
    }


    private boolean listEquals(List<Person> list1,List<Person> list2){
        if (list1 == list2)
            return true;

        Iterator<Person> e1 = list1.iterator();
        Iterator<Person> e2 = list2.iterator();


        while (e1.hasNext() && e2.hasNext()) {
            Person o1 = e1.next();
            Person o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
}
