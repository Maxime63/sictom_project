//Initilisation de la carte
var map;

//Initilisation du trajet du camion
var fullPath;

//Initialisation du tableau des markers.
var markers = [];

//Nombre de markers
var nbMarkers = 0;

//Options de la map
var mapOptions = {
    center: new google.maps.LatLng(45.777222, 3.087025),
    zoom: 10
};

//Option pour dessiner le chemin.
var fullPathOptions = {
    strokeColor: '#000000',
    strokeOpacity: 1.0,
    strokeWeight: 3
};

//évènement au chargement de la page.
google.maps.event.addDomListener(window, 'load', initialize);

//Cette fonction permet d'initialiser la carte google map sur la page. 
function initialize() {
       //Initialisation de la carte
    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

    //Evènement au clique sur la map.
    google.maps.event.addListener(map, 'click', function(e) {
        placeMarker(e.latLng);
    });

    fullPath = new google.maps.Polyline(fullPathOptions);
    fullPath.setMap(map);
}

//Cette function permet d'ajouter un point sur la map.
//Cette function ajoute un point à la liste et relie les points un à un dans l'ordre où ils ont été sélectionné.
function placeMarker(localisation){
    //Création du trait
    var path = fullPath.getPath();
    path.push(localisation);

    nbMarkers++;

    //Création du marker
    var marker = new google.maps.Marker({
        position: localisation,
        map: map,
    });

    //Ajout du nouveau dans le tableau des markers
    markers.push(marker);

    //Ajout du point sur la map
    map.panTo(localisation);

    //Chargement des input pour avoir les données.
    addRow(marker);
}

//Fonction qui permet de supprimer les markers présents sur la map.
//La fonction supprime également les traits qui relient les différents points.
function deleteMarkers(){
    var table = document.getElementById("markersT");

    //Suppression des markers
    for(var i = 0; i < markers.length; i++){
        markers[i].setMap(null);
        table.deleteRow(-1);
    }
    markers = [];
    nbMarkers = 0;

    //Suppression du chemin
    fullPath.setPath([]);
}

//Cette fonction permet d'ajouter les coordonnées d'un point dans le tableau.
function addRow(marker){
    var table = document.getElementById("markersT");
    var row = table.insertRow(-1);

    if(nbMarkers%2 == 0){
        row.className += "even";
    }


    var columnNb = row.insertCell(0);
    var columnLat = row.insertCell(1);
    var columnLng = row.insertCell(1);

    columnNb.innerHTML += nbMarkers;
    columnLat.innerHTML += marker.getPosition().lat();
    columnLng.innerHTML += marker.getPosition().lng();
}