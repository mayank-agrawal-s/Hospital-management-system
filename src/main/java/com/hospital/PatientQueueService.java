
package com.hospital;


import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

@Service
public class PatientQueueService {

    private final Queue<Integer> patientQueue = new LinkedList<>();

    public void addPatient(Integer patientId) {
        if (patientId != null && !patientQueue.contains(patientId)) {
            patientQueue.offer(patientId);
        }
    }

    public Integer callNextPatient() {
        return patientQueue.poll();
    }

    public List<Integer> getWaitingQueue() {
        return new ArrayList<>(patientQueue);
    }

    public int getQueueSize() {
        return patientQueue.size();
    }

    public boolean removePatient(Integer patientId) {
        return patientQueue.remove(patientId);
    }

    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }
}
