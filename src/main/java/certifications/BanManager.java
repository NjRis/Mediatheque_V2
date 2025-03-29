package certifications;

import model.Abonne;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;

public class BanManager {
    private static final ConcurrentHashMap<Integer, LocalDateTime> bannis = new ConcurrentHashMap<>();

    public static void verifierBanissement(Abonne emprunteur, LocalDateTime dateEmprunt, boolean estEndommage) {
        if (emprunteur == null || dateEmprunt == null) return;

        long retard = Duration.between(dateEmprunt.plusWeeks(2), LocalDateTime.now()).toDays();
        if (retard > 0 || estEndommage) {
            bannis.put(emprunteur.getNumero(), LocalDateTime.now().plusMonths(1));
        }
    }

    public static boolean estBanni(int numeroAbonne) {
        return bannis.getOrDefault(numeroAbonne, LocalDateTime.MIN).isAfter(LocalDateTime.now());
    }
    public static LocalDateTime getDateFinBan(int numAbonne) {
        return bannis.getOrDefault(numAbonne, LocalDateTime.MIN);
    }
}