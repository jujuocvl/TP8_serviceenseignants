package champollion;

import java.util.Date;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChampollionJUnitTest {
	Enseignant enseignant;
	UE uml, java;
		
	@BeforeEach
	public void setUp() {
		enseignant = new Enseignant("untel", "untel@gmail.com");
		uml = new UE("UML");
		java = new UE("Programmation en java");		
	}

	@Test
	void ajoutInterventionChangeResteAPlanifier() {
		// L'enseignant a un service prévu de 10h TD en UML
		enseignant.ajouteEnseignement(uml, 0, 10, 0);
		// Rien n'est encore planifié, il reste donc 10h de TD en UML à planifier
		assertEquals(10, enseignant.resteAPlanifier(uml, TypeIntervention.TD));
		// On planifie une intervention de 1h TD en UML
		Intervention inter = new Intervention(1, new Date(), enseignant, uml, TypeIntervention.TD, null);
		enseignant.ajouteIntervention(inter);
		// Il reste maintenant 9h TD à planifier en UML
		assertEquals(9, enseignant.resteAPlanifier(uml, TypeIntervention.TD));
	}

	@Test
	void heuresPlanifiees() {
		assertEquals(0, enseignant.heuresPlanifiee());
		// L'enseignant a un service prévu de 10h TD en UML
		enseignant.ajouteEnseignement(uml, 0, 10, 0);
		// On planifie une intervention de 1h TD en UML
		Intervention inter = new Intervention(1, new Date(), enseignant, uml, TypeIntervention.TD, null);
		enseignant.ajouteIntervention(inter);
		assertEquals(1, enseignant.heuresPlanifiee());
	}

	@Test
	void ajoutInterventionNecessiteServicePrevuDansUE() {
		// L'enseignant a un service prévu de 10h TD en UML
		enseignant.ajouteEnseignement(uml, 0, 10, 0);
		// On crée une intervention de 1h en java pour cet enseignant
		Intervention inter = new Intervention(1, new Date(), enseignant, java, TypeIntervention.TD, null);
		
		try {
			// L'enseignant n'a pas de service prévu en java
			enseignant.ajouteIntervention(inter);
			// La ligne ci-dessus DOIT lever une exception
			// Si on arrive ici c'est une erreur, le test échoue
			fail("L'enseignant n'a pas de service prévu dans cette UE");
		} catch (IllegalArgumentException ex) {
			// Si on arrive ici c'est normal
		}
	}
	

	@Test
	public void testNouvelEnseignantSansService() {
		assertEquals(0, enseignant.heuresPrevuesPourUE(uml));
		// Un nouvel enseignant n'a pas d'heure au total
        assertEquals(0, enseignant.heuresPrevues());
	}

	@Test
	void nouvelEnseignantSansUE() {
		assertTrue( enseignant.UEPrevues().isEmpty() );
	}

	@Test
	void nouvelEnseignantEnSousService() {
		assertTrue( enseignant.enSousService() );
	}
	
	@Test
	public void testAjouteHeures() {
		//Au début, aucune heure prévue
		assertEquals(0, enseignant.heuresPrevues());
		enseignant.ajouteEnseignement(uml, 0, 10, 0);
		// Il a maintenant 10 heures prévues pour cette UE
		assertEquals(10, enseignant.heuresPrevuesPourUE(uml));
		enseignant.ajouteEnseignement(uml, 0, 20, 0);
		// Il a maintenant 10 + 20 heures prévues pour cette UE
		assertEquals(30, enseignant.heuresPrevuesPourUE(uml));		
                assertEquals(30, enseignant.heuresPrevues());
	}

	@Test
	void ajoutHeuresNegativesImpossible() {
		try {
			// La ligne ci-dessous DOIT lever une exception
			enseignant.ajouteEnseignement(uml, 0, -1, 0);
			// Si on arrive ici, c'est une erreur
			// On force l'échec du test
			fail("La valeur négative aurait du être refusée");
		} catch (IllegalArgumentException ex) {
			// Si on arrive ici c'est normal, c'est ce qu'on veut
		}
	}

	@Test
	void calculeCorrectementLeService() {
		// 10hTD -> 10 équivalent TD
		enseignant.ajouteEnseignement(uml, 0, 10, 0);
		// 20hCM -> 30 équivalent TD
		enseignant.ajouteEnseignement(uml, 20, 0, 0);
		// 20hTP -> 15 équivalent TD
		enseignant.ajouteEnseignement(java, 0, 0, 20);
		
		int expected = 10 + 30 + 15;
                assertEquals(expected, enseignant.heuresPrevues());		
	}
	
}
