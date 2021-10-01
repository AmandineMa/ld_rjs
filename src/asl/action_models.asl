//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).
// TODO add ~hasInLeftHand(Human,_)
actionModel(pick(Human,Pickable),[isOnTopOf(Pickable,Container)],[handMovingToward(Human,PickableList)],[~isOnTopOf(Pickable,Support)],[isHolding(Human,Pickable)]).
//actionModel(pick(Human,Pickable),[isInContainer(Pickable,Container)],[rightHandMovingTowards(Human,PickableList)],[hasInLeftHand(Human,Pickable)],[~isInContainer(Pickable,Container)]).
//TODO should wait for both necessary effects and not only one
actionModel(place(Human,Pickable,Support),[isHolding(Human,Pickable)],[handMovingToward(Human,SupportList)],[~isHolding(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

actionModel(placeStick(Human,Pickable,Support1,Support2),[isHolding(Human,Pickable)],[handMovingToward(Human,SupportList)],[~isHolding(Human,Pickable)],[isOnTopOf(Pickable,Support1),isOnTopOf(Pickable,Support2)]).
//actionModel(pickAndPlace(Human,Pickable,Support),[isOnTopOf(Pickable,Support)],[rightHandMovingTowards(Human,SupportList)],[~hasInLeftHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).
//
//actionModel(pickAndPlaceStick(Human,Pickable,Support1,Support2),[isOnTopOf(Pickable,Support)],[rightHandMovingTowards(Human,SupportList)],[~hasInLeftHand(Human,Pickable)],[isOnTopOf(Pickable,Support1),isOnTopOf(Pickable,Support2)]).
//
//actionModel(drop(Human,Pickable,Container),[hasInLeftHand(Human,Pickable)],[rightHandMovingTowards(Human,ContainerList)],[~hasInLeftHand(Human,Pickable)],[isIn(Pickable,Container)]).
//
//actionModel(openContainer(Human,Drawer),[],[rightHandMovingTowards(Human,DrawerList)],[hasInLeftHand(Human,Drawer)],[isOpen(Drawer,_)]).
//
//actionModel(scan(Human,Pickable),[holding(Human,Scanner)],[rightHandMovingTowards(Human,PickableList)],[hasInLeftHand(Human,Pickable)],[isScanned(Pickable,_)]).
//
//actionModel(goTo(Human,Place),[],[moving(Human,_)],[],[]).
//
//actionModel(leave(Human),[],[moving(Human,_)],[],[isSeeing(Robot,Human)]).

//actionModel(wait(Human),[],[],[],[]).

//actionModel(ActPred,Effects)).
actionModel(pick(Robot,[Pickable]),[hasInLeftHand(Robot,Pickable), ~isOnTopOf(Pickable,Support)])[robot].

actionModel(pickanddrop(Robot,[Pickable,Container]),[isIn(Pickable,Container)])[robot]. //action remove

actionModel(place(Robot,[Pickable,Support]),[~hasInLeftHand(Robot,Pickable), isOnTopOf(Pickable,Support)])[robot].

actionModel(drop(Robot,[Pickable,Container]),[~hasInLeftHand(Robot,Pickable), isIn(Pickable,Container)])[robot].

actionModel(scan(Robot,[Pickable]), [isScanned(Pickable,_)])[robot].

actionModel(goTo(Robot,[Place]),[],[moving(Human,_)],[],[])[robot].