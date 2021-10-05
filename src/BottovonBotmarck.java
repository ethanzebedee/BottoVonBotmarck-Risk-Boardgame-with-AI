
// Created by Ethan Hammond
// Methods made by Cathal � Faol�in, Ethan Hammond and Daniel McCarthy
import java.util.ArrayList;



public class BottovonBotmarck implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example
	
	private BoardAPI board;
	private PlayerAPI player;
	private ArrayList<Country> countries;
	private ArrayList<Opponent> opponents;
	
	
	BottovonBotmarck (BoardAPI inBoard, PlayerAPI inPlayer) {
		
		board = inBoard;
		player = inPlayer;
		// put your code here
		countries = createCountriesList();
		opponents = createOpponentsList();
	}
	
	
	public String getName() {
		
		String command = "";
		// put your code here
		command = "BottoVonBotmarck";
		return (command);
	}
	
	
	public String getReinforcement() {
		
		String command = "";
		// put your code here
		String country = opponents.get(player.getId()).getRandomOwned();
		country = country;
		command = country + " 1";
		return (command);
	}
	
	
	public String getPlacement(int forPlayer) {
		
		String command = "";
		// put your code here
		String country = opponents.get(forPlayer).getRandomOwned();
		command = country;
		return (command);
	}
	
	
	public String getCardExchange() {
		
		String command = "";
		
		// put your code here
		if (player.isForcedExchange()) {
			
			command = getTradeIns();
		} else {
			
			command = "skip";
		}
		
		return (command);
	}
	
	
	public String getBattle() {
		
		String command = "";
		// put your code here
		ArrayList<Attack> optimalAttacks = opponents.get(player.getId()).getOptimalAttacks();
		
		if (optimalAttacks.size() != 0) {
			
			Attack attack = optimalAttacks.get((int) (Math.random() * optimalAttacks.size() - 1));
			String attackCountryName = attack.attacker.name;
			String defendCountryName = attack.defender.name;
			command = attackCountryName
						+ " "
						+ defendCountryName
						+ " "
						+ attack.attacker.maxAttackTroops();
		} else {
			
			command = "skip";
		}
		
		return (command);
	}
	
	
	public String getDefence(int countryId) {
		
		String command = "";
		
		if (board.getNumUnits(countryId) >= 2) {
			
			command += 2;
		} else {
			
			command += 1;
		}
		
		return (command);
	}
	
	
	public String getMoveIn(int attackCountryId) {
		
		String command = "";
		// Get the attacking Country
		Country attacking = new Country(attackCountryId);
		// Get the max number of adjacent enemy troops
		int borderingTroops = opponents.get(player.getId()).borderingTroops(attacking);
		// Get the number of units in the attackingCountry
		int attackingTroops = board.getNumUnits(attackCountryId);
		
		// If the bordering troops is -1, then the attacking country owns all the adjacents-leave
		// one and move the rest in
		if (borderingTroops == -1) {
			
			// Decrement by one to leave one unit behind
			attackingTroops--;
			command += attackingTroops;
		} else {
			
			// Otherwise leave enough troops behind to defend against an attack
			if (borderingTroops == 1 && attackingTroops > 2) {
				
				// If there is only 1 troop bordering the country, leave 2 units to defend
				command += attackingTroops - 2;
			} else if (borderingTroops < attackingTroops) {
				
				// If the attacker has more troops, leave behind enough to match the bordering
				// troops
				command += attackingTroops - borderingTroops;
			} else {
				
				// If the attacker has less troops, split the forces 50/50
				int half = attackingTroops / 2;
				command += half;
			}
			
		}
		
		return (command);
	}
	
	
	public String getFortify() {
		
		String command = "";
		// Find the country with the largest number of surplus units
		int mostUnitsIndex = largestPositiveDifference();
		
		// If this returns -1, all differences are negative- does not fortify
		if (mostUnitsIndex == -1) {
			
			command = "skip";
		} else {
			
			// We now have the name of the country to move troops out of
			command += countries.get(mostUnitsIndex).name;
			// Find the country in the country group where they are the most outnumbered(largest
			// negative difference)
			int leastUnitsIndex = largestNegativeDifference(countries.get(mostUnitsIndex));
			Country toFortify = new Country(leastUnitsIndex);
			// We now have the name of the country to move troops to
			command += " " + toFortify.name;
			// Get the number of troops bordering the mostUnitsIndex
			int borderingTroops = opponents.get(player.getId())
						.borderingTroops(countries.get(mostUnitsIndex));
			// Get the number of surplus troops
			int surplusTroops = countries.get(mostUnitsIndex).numUnits() - borderingTroops;
			// We now have the number of troops to move
			command += " " + surplusTroops;
		}
		
		// Find the country where the ratio between the bordering countries is greatest
		return (command);
	}
	
	
	// A method for finding the country with the largest positive difference between the number of
	// troops and their adjacencies
	private int largestPositiveDifference() {
		
		// Initialise the largestNumber of units found to -1
		int largestDifferenceFound = -1;
		int countryLargestDifference = -1;
		
		// Run through the controlled countries
		for (int i = 0; i < countries.size(); i++) {
			
			// Make sure that the player owns the country
			if (countries.get(i).owner() == player.getId()) {
				
				// Find the enemy adjacent with the most troops
				Country enemy =
							opponents.get(player.getId()).countryMostEnemyTroops(countries.get(i));
				// Get the ratio of enemy country to the controlled country
				Attack difference = new Attack(countries.get(i), enemy);
				
				// Check that the difference is positive
				if (difference.troopDifference() > 0) {
					
					// Check if the difference is greater then the current largestDifferenceFound-
					// if it is update
					if (difference.troopDifference() > largestDifferenceFound) {
						
						largestDifferenceFound = difference.troopDifference();
						countryLargestDifference = i;
					}
					
				}
				
			}
			
		}
		
		return countryLargestDifference;
	}
	
	
	// A method for finding the country with the largest negative difference between the number of
	// troops and their adjacencies
	// in a certain country group
	private int largestNegativeDifference(Country toFortifyWith) {
		
		// Initialise the largestNumber of units found to 1
		int largestNegativeDifferenceFound = 1;
		int countryLargestDifference = -1;
		// Generate the countryGroup
		ArrayList<Country> countryGroup = toFortifyWith.getCountryGroup();
		
		// Run through the controlled countries
		for (int i = 0; i < countryGroup.size(); i++) {
			
			// Find the enemy adjacent with the most troops
			Country enemy =
						opponents.get(player.getId()).countryMostEnemyTroops(countryGroup.get(i));
			// Get the ratio of enemy country to the controlled country
			Attack difference = new Attack(countryGroup.get(i), enemy);
			
			// If its the first difference, update the largestDifferenceFound
			if (i == 0) {
				
				largestNegativeDifferenceFound = difference.troopDifference();
				countryLargestDifference = 0;
			}
			// Check if the difference is larger (in the negative) then the current
			// largestDifferenceFound- if it is update
			else if (difference.troopDifference() < largestNegativeDifferenceFound) {
				
				largestNegativeDifferenceFound = difference.troopDifference();
				countryLargestDifference = countryGroup.get(i).index;
			}
			
		}
		
		return countryLargestDifference;
	}
	
	
	private String getTradeIns() {
		
		ArrayList<Card> botCards = player.getCards();
		int[] cardInsigniaArray = {0, 0, 0, 0}; // i, c, a, w;
		String[] cardLetterArray = {"i", "c", "a"};
		
		for (Card card: botCards) {
			
			cardInsigniaArray[card.getInsigniaId()]++;
		}
		
		int i;
		
		for (i = 0; i < 3; i++) {
			
			// If there are three cards of the same type, hand them in
			if (cardInsigniaArray[i] >= 3) {
				
				return cardLetterArray[i] + cardLetterArray[i] + cardLetterArray[i];
			}
			
		}
		
		// If there is at least one of each card, submit one of each card
		if (cardInsigniaArray[0] >= 1 && cardInsigniaArray[1] >= 1 && cardInsigniaArray[2] >= 1) {
			
			return "ica";
		}
		
		// If the wild card is greater then 1
		if (cardInsigniaArray[3] >= 1) {
			
			for (i = 0; i < 3; i++) {
				
				// If there is two cards of the same type, submit them with the wild card
				if (cardInsigniaArray[i] >= 2)
					return cardLetterArray[i] + cardLetterArray[i] + "w";
			}
			
			if (cardInsigniaArray[0] >= 1 && cardInsigniaArray[1] >= 1) { return "icw"; }
			
			if (cardInsigniaArray[1] >= 1 && cardInsigniaArray[2] >= 1) { return "wca"; }
			
			if (cardInsigniaArray[0] >= 1 && cardInsigniaArray[2] >= 1) { return "iwc"; }
			
		}
		
		if (cardInsigniaArray[3] >= 2) {
			
			for (i = 0; i < 3; i++) {
				
				if (cardInsigniaArray[i] >= 1)
					return cardLetterArray[i] + "ww";
			}
			
		}
		
		return "";
	}
	
	
	private ArrayList<Country> createCountriesList() {
		
		ArrayList<Country> list = new ArrayList<Country>();
		
		for (int i = 0; i < GameData.NUM_COUNTRIES; i++) {
			
			list.add(new Country(i));
		}
		
		return list;
	}
	
	
	private ArrayList<Opponent> createOpponentsList() {
		
		ArrayList<Opponent> list = new ArrayList<Opponent>();
		
		for (int i = 0; i < GameData.NUM_PLAYERS_PLUS_NEUTRALS; i++) {
			
			list.add(new Opponent(i));
		}
		
		return list;
	}
	
	
	class Attack {
		
		Country attacker;
		Country defender;
		
		
		public Attack (Country a, Country d) {
			
			attacker = a;
			defender = d;
		}
		
		
		public int offensiveForce() {
			
			return attacker.numUnits() - 1;
		}
		
		
		public int defensiveForce() {
			
			return defender.numUnits();
		}
		
		
		public int troopDifference() {
			
			return attacker.numUnits() - defender.numUnits();
		}
		
		
		public double troopRatio() {
			
			return (double) attacker.numUnits() / defender.numUnits();
		}
		
		
		public Boolean isPossible() {
			
			if (attacker.index != defender.index && attacker.owner() != defender.owner()
						&& attacker.isAdjacent(defender)
						&& attacker.numUnits() > 1) {
				
				return true;
			}
			
			return false;
		}
		
		
		public String toString() {
			
			return attacker.owner()
						+ ": "
						+ attacker.name
						+ "("
						+ attacker.numUnits()
						+ ")"
						+ " -> "
						+
						defender.owner()
						+ ": "
						+ defender.name
						+ "("
						+ defender.numUnits()
						+ ")"
						+ ", "
						+
						troopRatio();
		}
		
	}
	
	
	
	// class to keep track of other players
	class Opponent {
		
		public int index;
		
		
		public Opponent (int Index) {
			
			index = Index;
		}
		
		
		public String getRandomOwned() {
			
			ArrayList<Country> owned = ownedCountries();
			
			if (owned.size() > 0) {
				
				return owned.get((int) ((Math.random() * owned.size()) - 1)).name;
			} else {
				
				return "";
			}
			
		}
		
		
		public ArrayList<Country> ownedCountries() {
			
			ArrayList<Country> ownedTerritories = new ArrayList<Country>();
			
			for (Country country: countries) {
				
				if (country.owner() == index) {
					
					ownedTerritories.add(country);
				}
				
			}
			
			return ownedTerritories;
		}
		
		
		public ArrayList<Attack> getPossibleAttacks() {
			
			ArrayList<Attack> attacks = new ArrayList<Attack>();
			
			for (Country country: ownedCountries()) {
				
				for (int i = 0; i < country.adjacents.length; i++) {
					
					Attack a = new Attack(country, countries.get(country.adjacents[i]));
					
					if (a.isPossible()) {
						
						attacks.add(a);
					}
					
				}
				
			}
			
			return attacks;
		}
		
		
		public ArrayList<Attack> getOptimalAttacks() {
			
			ArrayList<Attack> optimalAttacks = new ArrayList<Attack>();
			
			for (Attack attacks: getPossibleAttacks()) {
				
				if (attacks.defensiveForce() == 1 && attacks.offensiveForce() >= 2) {
					
					optimalAttacks.add(attacks);
				} else if (attacks.troopDifference() >= 3) {
					
					optimalAttacks.add(attacks);
				}
				
			}
			
			return optimalAttacks;
		}
		
		
		public ArrayList<Country> getEnemyNeighbors(int enemy) {
			
			ArrayList<Country> borderEnemies = new ArrayList<Country>();
			
			for (Country botCountry: ownedCountries()) {
				
				for (int i = 0; i < botCountry.adjacents.length; i++) {
					
					// if neighboring country is owned by enemy
					Country borderCountry = countries.get(botCountry.adjacents[i]);
					
					if (borderCountry.owner() == enemy) {
						
						borderEnemies.add(borderCountry);
					}
					
				}
				
			}
			
			return borderEnemies;
		}
		
		
		// This finds the enemy adjacency with the greatest number of troops
		public Country countryMostEnemyTroops(Country controlled) {
			
			// The max troop number of enemy bordering troops found
			int maxTroops = -1;
			Country result = controlled;
			
			for (int i = 0; i < controlled.adjacents.length; i++) {
				
				Country adjacent = new Country(controlled.adjacents[i]);
				
				// If the occupiers of the country and the adjacent is different then update the
				// max_troops
				if (controlled.owner() != adjacent.owner()) {
					
					// Check that the number of troops in the adjacent tile is greater then the
					// current max_troops then update
					if (adjacent.numUnits() > maxTroops) {
						
						maxTroops = adjacent.numUnits();
						result = adjacent;
					}
					
				}
				
			}
			
			return result;
		}
		
		
		// This returns the highest number of bordering
		// troops on any of the tiles. If they all belong to the owner, it returns -1
		public int borderingTroops(Country controlled) {
			
			Country hasMostTroops = countryMostEnemyTroops(controlled);
			
			if (hasMostTroops == controlled) {
				
				return -1;
			} else {
				
				return hasMostTroops.numUnits();
			}
			
		}
		
	}
	
	
	
	class Country {
		
		public int index;
		public String name;
		public int continent;
		public int[] adjacents;
		
		
		public Country (int Index) {
			
			index = Index;
			name = GameData.COUNTRY_NAMES[index].replaceAll("\\s", "");;
			continent = GameData.CONTINENT_IDS[index];
			adjacents = GameData.ADJACENT[index];
		}
		
		
		public int owner() {
			
			return board.getOccupier(index);
		}
		
		
		public Boolean hasOwner() {
			
			return board.isOccupied(index);
		}
		
		
		public int numUnits() {
			
			return board.getNumUnits(index);
		}
		
		
		public ArrayList<Country> getOwnedAdjacents() {
			
			return getOwnedAdjacents(new ArrayList<Country>());
		}
		
		
		public int maxAttackTroops() {
			
			if (numUnits() > 3) {
				
				return 3;
			} else {
				
				return numUnits() - 1;
			}
			
		}
		
		
		// check if adjacent to another country
		public Boolean isAdjacent(Country country) {
			
			for (int i = 0; i < adjacents.length; i++) {
				
				if (country.index == adjacents[i]) { return true; }
				
			}
			
			return false;
		}
		
		
		// get the neighboring countries owned by the same person as this country
		private ArrayList<Country> getOwnedAdjacents(ArrayList<Country> excludeList) {
			
			ArrayList<Country> adjacentsList = new ArrayList<Country>();
			
			for (int i = 0; i < adjacents.length; i++) {
				
				Country adj = countries.get(adjacents[i]);
				Boolean excl = false;
				
				for (Country excluded: excludeList) {
					
					if (excluded == adj) {
						
						excl = true;
					}
					
				}
				
				if (adj.owner() == owner() && excl == false) {
					
					adjacentsList.add(adj);
				}
				
			}
			
			return adjacentsList;
		}
		
		
		public ArrayList<Country> getCountryGroup() {
			
			return getCountryGroup(new ArrayList<Country>());
		}
		
		
		// get the group of countries that this country belongs to - ie all of the countries
		// connected and owned by the same player
		private ArrayList<Country> getCountryGroup(ArrayList<Country> excludeList) {
			
			ArrayList<Country> thisCountry = new ArrayList<Country>();
			thisCountry.add(this);
			ArrayList<Country> countryGroup =
						mergeCountryGroups(new ArrayList<Country>(), thisCountry);
			ArrayList<Country> ownedAdjacents = getOwnedAdjacents(excludeList);
			
			if (ownedAdjacents.size() > 0) {
				
				countryGroup =
							mergeCountryGroups(countryGroup, adj.getCountryGroup(countryGroup));
			}
			
			return countryGroup;
		}
		
		
		public ArrayList<Country> mergeCountryGroups(ArrayList<Country> g1, ArrayList<Country> g2) {
			
			for (Country member: g2) {
				
				// if g1 doesnt already contain member of g2
				if (!groupHasCountry(g1, member)) {
					
					g1.add(member);
				}
				
			}
			
			return g1;
		}
		
		
		// check if group of contries contains given country
		public Boolean groupHasCountry(ArrayList<Country> group, Country country) {
			
			for (Country member: group) {
				
				if (member == country) { return true; }
				
			}
			
			return false;
		}
		
		
		public String toString() {
			
			return name + ": " + numUnits();
		}
		
	}
	
	
	
	class CountryGroup {
		
		public ArrayList<Country> list;
		
		
		CountryGroup (ArrayList<Country> g) {
			
			list = g;
		}
		
		
		public int size() {
			
			return list.size();
		}
		
		
		public int owner() {
			
			if (list.size() == 0) {
				
				return -1;
			} else {
				
				return list.get(0).owner();
			}
			
		}
		
		
		public int totalUnits() {
			
			int total = 0;
			
			for (Country country: list) {
				
				total += country.numUnits();
			}
			
			return total;
		}
		
		
		// check if certain country is contained in countrygroup
		public Boolean hasCountry(Country country) {
			
			for (Country member: list) {
				
				if (member == country) { return true; }
				
			}
			
			return false;
		}
		
	}
	
}
