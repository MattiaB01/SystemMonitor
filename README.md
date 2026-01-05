# System Monitor

Progetto minimale in Java che monitora in tempo reale l'utilizzo di CPU, RAM e spazio su disco del PC, visualizza lo stato tramite una semplice interfaccia grafica e invia i dati tramite **MQTT** per la visualizzazione su un display ESP32.

---

## Scopo

- Monitorare le risorse di sistema (CPU, RAM, spazio disco) del PC.
- Mostrare valori aggiornati in tempo reale con una **piccola interfaccia grafica**.
- Inviare dati su broker MQTT per display remoti (es. [ESP32 System Monitor](https://github.com/MattiaB01/Esp32_System_Monitor_Display)).

---

## Funzionalità

- Lettura periodica di:
  - Percentuale CPU
  - Percentuale RAM
  - Spazio su disco
- Visualizzazione grafica in finestra Swing
- Invio periodico dei dati tramite MQTT su tre topic separati:
  - `system/cpu`
  - `system/ram`
  - `system/space`
- Logging dei dati e degli eventi

---

## Requisiti

- **Java 17 o superiore**
- **Broker MQTT** (es. Mosquitto, EMQX)
- Librerie Java necessarie (aggiungere al classpath o tramite Maven):
  - SLF4J (per logging)
  - Eclipse Paho MQTT client

---
Necessita di broker MQTT attivo (es. Mosquitto su PC o rete locale).
![Cpu](https://github.com/user-attachments/assets/39cf8e4c-e0c9-41de-9e6f-aefd42ba9990)
