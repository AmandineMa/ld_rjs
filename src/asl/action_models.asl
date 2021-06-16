//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).

actionModel(pick(Human,Pickable),[handEmpty(Human,_),isOnTopOf(Pickable,Support)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[~isOnTopOf(Pickable,Support)]).

actionModel(place(Human,Pickable,Support),[hasInHand(Human,Pickable)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

actionModel(pickAndPlace(Human,Pickable,Support),[handEmpty(Human,_),isOnTopOf(Pickable,Support)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

actionModel(pickAndPlaceStick(Human,Pickable,Support1,Support2),[handEmpty(Human,_),isOnTopOf(Pickable,Support)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOnTopOf(Pickable,Support1),isOnTopOf(Pickable,Support2)]).

actionModel(drop(Human,Pickable,Container),[hasInHand(Human,Pickable)],[handMovingToward(Human,ContainerList)],[~hasInHand(Human,Pickable)],[isIn(Pickable,Container)]).

actionModel(openContainer(Human,Drawer),[handEmpty(Human,_)],[handMovingToward(Human,DrawerList)],[hasInHand(Human,Drawer)],[isOpen(Drawer,_)]).

actionModel(scan(Human,Pickable),[holding(Human,Scanner)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[isScanned(Pickable,_)]).

actionModel(goTo(Human,Place),[],[moving(Human,_)],[],[]).

actionModel(leave(Human),[],[moving(Human,_)],[],[isSeeing(Robot,Human)]).

//actionModel(ActPred,Effects)).
actionModel(pick(Robot,[Pickable]),[isHolding(Robot,Pickable), ~isOnTopOf(Pickable,Support)])[robot].

actionModel(pickanddrop(Robot,[Pickable,Container]),[isIn(Pickable,Container)])[robot]. //action remove

actionModel(place(Robot,[Pickable,Support]),[~isHolding(Robot,Pickable), isOnTopOf(Pickable,Support)])[robot].

actionModel(drop(Robot,[Pickable,Container]),[~isHolding(Robot,Pickable), isIn(Pickable,Container)])[robot].

actionModel(scan(Robot,[Pickable]), [isScanned(Pickable,_)])[robot].

actionModel(goTo(Robot,[Place]),[],[moving(Human,_)],[],[])[robot].