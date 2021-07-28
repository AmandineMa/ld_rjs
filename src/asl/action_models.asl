//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).
// TODO add ~hasInRightHand(Human,_)
actionModel(pick(Human,Pickable),[isInContainer(Pickable,Container)],[rightHandMovingTowards(Human,PickableList)],[],[hasInRightHand(Human,Pickable)]).
//actionModel(pick(Human,Pickable),[isInContainer(Pickable,Container)],[rightHandMovingTowards(Human,PickableList)],[hasInRightHand(Human,Pickable)],[~isInContainer(Pickable,Container)]).

actionModel(place(Human,Pickable,Support),[hasInRightHand(Human,Pickable)],[rightHandMovingTowards(Human,SupportList)],[~hasInRightHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

//actionModel(pickAndPlace(Human,Pickable,Support),[isOnTopOf(Pickable,Support)],[rightHandMovingTowards(Human,SupportList)],[~hasInRightHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).
//
//actionModel(pickAndPlaceStick(Human,Pickable,Support1,Support2),[isOnTopOf(Pickable,Support)],[rightHandMovingTowards(Human,SupportList)],[~hasInRightHand(Human,Pickable)],[isOnTopOf(Pickable,Support1),isOnTopOf(Pickable,Support2)]).
//
//actionModel(drop(Human,Pickable,Container),[hasInRightHand(Human,Pickable)],[rightHandMovingTowards(Human,ContainerList)],[~hasInRightHand(Human,Pickable)],[isIn(Pickable,Container)]).
//
//actionModel(openContainer(Human,Drawer),[],[rightHandMovingTowards(Human,DrawerList)],[hasInRightHand(Human,Drawer)],[isOpen(Drawer,_)]).
//
//actionModel(scan(Human,Pickable),[holding(Human,Scanner)],[rightHandMovingTowards(Human,PickableList)],[hasInRightHand(Human,Pickable)],[isScanned(Pickable,_)]).
//
//actionModel(goTo(Human,Place),[],[moving(Human,_)],[],[]).
//
//actionModel(leave(Human),[],[moving(Human,_)],[],[isSeeing(Robot,Human)]).

//actionModel(ActPred,Effects)).
actionModel(pick(Robot,[Pickable]),[isHolding(Robot,Pickable), ~isOnTopOf(Pickable,Support)])[robot].

actionModel(pickanddrop(Robot,[Pickable,Container]),[isIn(Pickable,Container)])[robot]. //action remove

actionModel(place(Robot,[Pickable,Support]),[~isHolding(Robot,Pickable), isOnTopOf(Pickable,Support)])[robot].

actionModel(drop(Robot,[Pickable,Container]),[~isHolding(Robot,Pickable), isIn(Pickable,Container)])[robot].

actionModel(scan(Robot,[Pickable]), [isScanned(Pickable,_)])[robot].

actionModel(goTo(Robot,[Place]),[],[moving(Human,_)],[],[])[robot].