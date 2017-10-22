package interfaces;

import jade.core.AID;

public interface IOwnable {
	 AID getOwner();
	 void setOwner(AID owner);
}
