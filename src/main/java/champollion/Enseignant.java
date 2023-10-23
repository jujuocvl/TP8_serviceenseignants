package champollion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Enseignant extends Personne {

    private final Set<Intervention> planification = new HashSet<>();

    private final Map<UE, ServicePrevu> enseignements = new HashMap<>();

    public Enseignant(String nom, String email) {
        super(nom, email);
    }

    public void ajouteIntervention(Intervention inter) {
		if (!enseignements.containsKey(inter.getMatiere())) {
			throw new IllegalArgumentException("La matière ne fait pas partie des enseignements");
		}

		planification.add(inter);
	}

	public int heuresPlanifiee() {
		float result = 0f;
		for (Intervention inter : planification) {
			result += equivalentTD(inter.getType(), inter.getDuree());
		}

		return Math.round(result);
	}

	private float equivalentTD(TypeIntervention type, int volumeHoraire) {
		float result = 0f;
		switch (type) {
			case CM:
				result = volumeHoraire * 1.5f;
				break;
			case TD:
				result = volumeHoraire;
				break;
			case TP:
				result = volumeHoraire * 0.75f;
				break;
		}
		return result;
	}
	
	public boolean enSousService() {
		return heuresPrevues() < 192;
	}

    /*
     * Calcule le nb total d'heures prévues pour enseignant
     * 1h CM = 1,5 h "équivalent TD" 
     * 1h TD = 1h "équivalent TD" 
     * 1h TP = 0,75h "équivalent TD"
     */
    public int heuresPrevues() {
        float result = 0;
        for (UE ue : enseignements.keySet()) {
			result += heuresPrevuesPourUE(ue);
		}
		return Math.round(result);
    }

    /*
     * Calcule le nb total d'heures prévues pour enseignant dans l'UE 
     */
    public int heuresPrevuesPourUE(UE ue) {
        float result = 0;

		ServicePrevu p = enseignements.get(ue);
		if (p != null) { // La clé existe, l'enseignant intervient dans l'UE
			result =  equivalentTD(TypeIntervention.CM, p.getVolumeCM())
				+ equivalentTD(TypeIntervention.TD, p.getVolumeTD())
				+ equivalentTD(TypeIntervention.TP, p.getVolumeTP())
			;
		}
		return Math.round(result);
    }

    /*
     * Ajoute un enseignement au service prévu pour enseignant
     */
    public void ajouteEnseignement(UE ue, int volumeCM, int volumeTD, int volumeTP) {
        if (volumeCM < 0 || volumeTD < 0 || volumeTP < 0) {
			throw new IllegalArgumentException("Les valeurs doivent être positives ou nulles");
		}

		ServicePrevu s = enseignements.get(ue);
		if (s == null) { 
			s = new ServicePrevu(volumeCM, volumeTD, volumeTP, ue);
			enseignements.put(ue, s);
		} else {
			s.setVolumeCM(volumeCM + s.getVolumeCM());
			s.setVolumeTD(volumeTD + s.getVolumeTD());
			s.setVolumeTP(volumeTP + s.getVolumeTP());
		}
	}
      
    public Set<UE> UEPrevues() {
		return enseignements.keySet();
	}

	public int resteAPlanifier(UE ue, TypeIntervention type) {
		float planifiees = 0f;
		ServicePrevu p = enseignements.get(ue);
		if (null == p) 
			return 0;
		
		float aPlanifier = p.getVolumePour(type);

		for (Intervention inter : planification) {
			if ((ue.equals(inter.getMatiere())) && (type.equals(inter.getType()))) {
				planifiees += inter.getDuree();
			}
		}
		return Math.round(aPlanifier - planifiees);
	}
}
