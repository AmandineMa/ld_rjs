//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).

actionModel(pick(Human,Pickable),[handEmpty(Human,_),isOnTopOf(Pickable,Support)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[~isOnTopOf(Pickable,Support)]).

actionModel(place(Human,Pickable,Support),[hasInHand(Human,Pickable)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOnTopOf(Pickable,Support)]).

actionModel(drop(Human,Pickable,Container),[hasInHand(Human,Pickable)],[handMovingToward(Human,ContainerList)],[~hasInHand(Human,Pickable)],[isIn(Pickable,Container)]).

actionModel(openContainer(Human,Drawer),[handEmpty(Human,_)],[handMovingToward(Human,DrawerList)],[hasInHand(Human,Drawer)],[isOpen(Drawer,_)]).

actionModel(scan(Human,Pickable),[holding(Human,Scanner)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[isScanned(Pickable,_)]).

actionModel(goTo(Human,Place),[],[moving(Human,_)],[],[]).

actionModel(leave(Human),[],[moving(Human,_)],[],[isSeeing(Robot,Human)]).