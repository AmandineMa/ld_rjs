//action(ActPred,Preconditions,Movement,ProgressionEffects,NecessaryEffect).

action(pick(Human,Pickable),[handEmpty(Human),isOn(Pickable,Support)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[~isOn(Pickable,Support)]).

action(place(Human,Pickable,Support),[hasInHand(Human,Pickable)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOn(Pickable,Support)]).

action(drop(Human,Pickable,Container),[hasInHand(Human,Pickable)],[handMovingToward(Human,ContainerList)],[~hasInHand(Human,Pickable)],[isIn(Pickable,Container)]).

action(openContainer(Human,Drawer),[handEmpty(Human)],[handMovingToward(Human,DrawerList)],[hasInHand(Human,Drawer)],[isOpen(Drawer)]).

action(scan(Human,Pickable),[holding(Human,Scanner)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[isScanned(Pickable)]).

action(goTo(Human,Place),[],[moving(Human)],[],[]).

action(leave(Human),[],[moving(Human)],[],[isSeeing(Robot,Human)]).