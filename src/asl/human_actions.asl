//action(Name,Preconditions,Movement,ProgressionEffects,NecessaryEffect).

action(pick(Human,Pickable),[handEmpty(Human),isOn(Pickable,Support)],[handMovingToward(Human,PickableList)],[hasInHand(Human,Pickable)],[~isOn(Pickable,Support)]).

action(place(Human,Pickable,Support),[hasInHand(Human,Pickable)],[handMovingToward(Human,SupportList)],[~hasInHand(Human,Pickable)],[isOn(Pickable,Support)]).

action(throw(Human,Pickable,Box),[hasInHand(Human,Pickable)],[handMovingToward(Human,BoxList)],[~hasInHand(Human,Pickable)],[isIn(Pickable,Box)]).

action(openContainer(Human,Container),[handEmpty(Human)],[handMovingToward(Human,ContainerList),hasInHand(Human,Container)],[],[isOpen(Container)]).

action(scan(Human,Pickable),[holding(Human,Scanner)],[handMovingToward(Human,PickableList)],[],[isScanned(Pickable)]).

action(goTo(Human,Place),[],[moving(Human)],[],[]).

action(leave(Human),[],[moving(Human)],[],[isSeeing(Robot,Human)]).