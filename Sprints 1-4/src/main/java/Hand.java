import java.util.ArrayList;

public class Hand {
	
	public enum BonusType {NONE, INFANTRY, CAVALRY, ARTILLERY, ALL};
	
	private boolean condition;
	
	private ArrayList<Card> hand;

    //Instantiates player's hand
	public Hand() {
	
		hand = new ArrayList<Card>();
	}

    //returns the hand
	public ArrayList<Card> getCards() {
		return hand;
	}

	//Adds cards to player's deck
	public void add(Card card) {
	
		hand.add(card);
	}

    public void remove(Deck deckOfCards, int index){

        deckOfCards.add(hand.get(index));
        hand.remove(index);

    }
	

	//Checks if player can turn in cards
	public boolean canTurnInCards(int index1, int index2, int index3) {
	
        condition = false;

        if(!(hand.get(index1).getType().equals("Joker") || hand.get(index2).getType().equals("Joker") || hand.get(index3).getType().equals("Joker"))){
            
            if (hand.get(index1).getType().equals(hand.get(index2).getType()) && hand.get(index1).getType().equals(hand.get(index3).getType())) {
            //If all three cards have the same type
                condition = true;
                    
            } else if (!hand.get(index1).getType().equals(hand.get(index2).getType()) && !hand.get(index1).getType().equals(hand.get(index3).getType()) && !hand.get(index2).getType().equals(hand.get(index3).getType())) {
            //If all three cards have different types
                condition = true;
            }

        } else {

        //if any card is a joker, then all combinations are valid
            condition = true;

        }

        return condition;
	}

    public int cardBonus(int index1, int index2, int index3){

        int[] arr = {index1, index2, index3};
	BonusType bonus = BonusType.NONE;
		
	int infantryNum = 0, cavalryNum = 0, artilleryNum = 0;
	
	//Go through each index and increment the appropriate integers
	for(int i = 0; i < arr.length; i++) {
		switch(hand.get(arr[i]).getType()) {
		case "Infantry":
			infantryNum++;
			break;
		case "Cavalry":
			cavalryNum++;
			break;
		case "Artillery":
			artilleryNum++;
			break;
		default: //The Joker is a wild card, and counts for an instance of all three other types at once
			infantryNum++;
			cavalryNum++;
			artilleryNum++;
			break;
		}	
	}
	
	//If any one of the three integers has reached 3, then the player qualifies for a bonus
	if(infantryNum == 3) bonus = BonusType.INFANTRY; 
	else if(cavalryNum == 3) bonus = BonusType.CAVALRY;
	else if(artilleryNum == 3) bonus = BonusType.ARTILLERY;
	//Otherwise, if the player has at least one instance of each card, they qualify for an "ALL" bonus
	else if(infantryNum >= 1 && cavalryNum >= 1 && artilleryNum >= 1) bonus = BonusType.ALL;
		
	return getBonusAmount(bonus);

    }
	
    //Returns the number of troops pertaining to a BonusType
    public int getBonusAmount(BonusType bonus) {
	int num;
		
	switch(bonus) {
	case INFANTRY:
		num = 4;
		break;
	case CAVALRY:
		num = 6;
		break;
	case ARTILLERY:
		num = 8;
		break;
	case ALL:
		num = 10;
		break;
	default: //BonusType = NONE
		num = 0;
		break;
	}
		
	return num;
   }

	//Checks if player has too many cards
	public void exceedHandSize() {
		
		if (hand.size() >= 5) {

            while(hand.size() > 5){

                hand.remove(hand.size() - 1);

            }
			
		}
		
	}

    public void printHand(Player player, UI ui){

        ui.displayString(player.getName() + "'s hand contains:");

        for(int i = 0; i < hand.size(); i++){
            ui.displayString(hand.get(i).getName() + " " + hand.get(i).getType() + " index:" + i);
        }

    }

    //Checks if a player controls the territory on any out of 3 cards
    public boolean checkControlsTerritory(int index1, int index2, int index3, Player player, Board board){
    	//Put the indexes in an array so they can be easily moved between 
    	int arr[]= {index1, index2, index3};
    	
    	//Check each of the cards 
    	for(int i=0;i<arr.length;i++) {
    		//Skip the joker, as it does not have a territory`	
    		if(!hand.get(arr[i]).getJoker()) {
    			//Get the territory int
    			int territory=Territory.validTerritory(hand.get(arr[i]).getName());
    		
    			//Check if the player owns the territory
    			if(board.getOccupier(territory)==player.getPlayerId()) {
    				return true;
    			}
    		}
    	}
    	
       //If you get to here, the player owns none of the terrritories
    	return false;
    }
    
}
    

