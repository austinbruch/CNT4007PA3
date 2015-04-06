#TODO

- [ ] Thorough Commenting
   - [X] LinkState
   - [X] Node
   - [ ] Router
   - [ ] StepPrinter
- [X] Fix Step Printing Layout
   - [X] StepPrinter
- [X] Move D and P lists, and N' set from LinkState to Router
   - [X] getDistanceFromNode
   - [X] updateDistance
   - [X] updatePValue
   - [X] Update StepPrinter to reflect these changes
   - [X] Update LinkState to reflect these changes
   - [X] Update Router to reflect these changes
- [ ] Consider moving access to D, P, and N' ArrayLists from StepPrinter through LinkState instead of Router
   - [ ] Not sure if StepPrinter should know about Router or not...