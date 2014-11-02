            function initialize() {
                //Position de Clermont Ferrand sur la carte google.
                var myLatlng = new google.maps.LatLng(45.777222, 3.087025);

                //Déclaration d'un marker
                var marker = new google.maps.Marker();

                var mapOptions = {
                    center: myLatlng,
                    zoom: 10
                };

                //Initilisation de la carte
                var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

                //Interception de l'event click sur la map.
                google.maps.event.addListener(map, 'click', function(e) {
                    placeMarker(e.latLng, map);
                });

                //Ajout d'un marker sur la map.
                function placeMarker(position, map){
                    //Suppression du marker.
                    marker.setMap(null);

                    //Ajout du marker
                    marker = new google.maps.Marker({
                        position: position,
                        map: map
                    });
                    map.panTo(position);

                    //Chargement des input pour avoir les données.
                    document.getElementById("latitude").value = marker.getPosition().lat();
                    document.getElementById("longitude").value = marker.getPosition().lng();
                }
            }
            google.maps.event.addDomListener(window, 'load', initialize);