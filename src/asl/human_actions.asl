//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).

action(pick(Human,Pickable),[handEmpty(Human,_),isOnTopOf(Pickable,Support)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[~isOnTopOf(Pickable,Support)]).

action(place(Human,Pickable,Support),[hasInHand(Human,Pickable)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

action(drop(Human,Pickable,Container),[hasInHand(Human,Pickable)],[handMovingToward(Human,ContainerList)],[~hasInHand(Human,Pickable)],[isIn(Pickable,Container)]).

action(openContainer(Human,Drawer),[handEmpty(Human,_)],[handMovingToward(Human,DrawerList)],[hasInHand(Human,Drawer)],[isOpen(Drawer,_)]).

action(scan(Human,Pickable),[holding(Human,Scanner)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[isScanned(Pickable,_)]).

action(goTo(Human,Place),[],[moving(Human,_)],[],[]).

action(leave(Human),[],[moving(Human,_)],[],[isSeeing(Robot,Human)]).