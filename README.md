# Silicon Valley: The Tech Cartel

## How To Run

## Class UML
```mermaid
classDiagram
direction TB

%% =========================
%% Main
%% =========================

class Main {
    +start(Stage)
}

%% =========================
%% Scene
%% =========================

class GameScene {
    +init() Scene
}

Main --> GameScene : creates

%% =========================
%% Main Components
%% =========================

class TopBar
class BottomBar
class Board
class PlayerPanel
class MarketPanel

%% =========================
%% Board
%% =========================

class InfoPanel

Board *-- InfoPanel

%% =========================
%% Buildings
%% =========================

class BuildingView {
    <<abstract>>
    #type : BuildingType
    #color : Color
    #owner : String
    #draw()*
}

%% =========================
%% Utility
%% =========================

class ElementStyle {
    <<utility>>
    +setMargin(...)
    +setLabelPos(...)
    +setCssStyle(...)
}

%% =========================
%% External Types
%% =========================

class VBox
class HBox
class Pane
class Application

%% =========================
%% Inheritance
%% =========================

Application <|-- Main

VBox <|-- Board
VBox <|-- PlayerPanel
VBox <|-- MarketPanel

HBox <|-- TopBar
HBox <|-- BottomBar
HBox <|-- InfoPanel

Pane <|-- BuildingView

%% =========================
%% Dependencies
%% =========================

TopBar ..> ElementStyle
BottomBar ..> ElementStyle
PlayerPanel ..> ElementStyle
MarketPanel ..> ElementStyle
```

## Game Logic

## Work Distribution
all works:
