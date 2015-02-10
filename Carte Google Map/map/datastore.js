//liste des camions
var camions;
var tournees;
var nomChauffeur;
var nomRipper1;
var nomRipper2;
var tournee_cour;

function compareCamion(a,b) {
  if (a.nom < b.nom)
     return -1;
  if (a.nom > b.nom)
    return 1;
  return 0;
}

function compareTournee(a, b)
{
  if (a.dateDebut < b.dateDebut)
     return 1;
  if (a.dateDebut > b.dateDebut)
    return -1;
  return 0;
}



function init() 
{
	  var rootApi = 'https://speedy-baton-840.appspot.com/_ah/api'; 
	  gapi.client.load('sictomApi', 'v1', function() 
	  {
		gapi.client.sictomApi.getAllCamions().execute(function(resp) 
		{
		if (!resp.code) 
		{
			var index;
			camions = resp.items;
			//On trie les camions sur leur nom pour les afficher dans l'ordre
			camions.sort(compareCamion);
			for (index = 0; index < camions.length; ++index) 
			{
				document.getElementById(index).text = camions[index].nom;
			}		
			
		}
	  });
	}, rootApi);			  
}

function getTournee(event)
{
	var idCam = camions[event.id].id;
	//Recherche des tournées
	gapi.client.sictomApi.getTourneesByCamion({
      'id': idCam
    }).execute(function(resp) 
		{
		if (!resp.code) 
		{
			tournees = resp.items;
			tournees.sort(compareTournee);
			if(tournees[0] != '')
			{
				document.getElementById('nomTournee').innerHTML = "Derni&egrave;re Tourn&eacute;e : " +  tournees[0].nom;
			}
			else
			{
				document.getElementById('nomTournee').innerHTML = "Tourn&eacute;e en cours : " +  tournees[0].nom;
			}
			tournee_cour = tournees[0];
			nomChauffeur = getNomUser(tournee_cour.chauffeurId,1);
			nomRipper1 = getNomUser(tournee_cour.firstRipperId,2);
			nomRipper2 = getNomUser(tournee_cour.secondRipperId,3);
			var div = document.getElementById("floatRight");
			div.style.visibility = 'visible';
			document.getElementById("nomCamion").innerHTML = camions[event.id].nom;
			document.getElementById("dateDep").innerHTML = tournee_cour.dateDebut;
			document.getElementById("dateArr").innerHTML = tournee_cour.dateFin;
			
			showTrajet(tournee_cour.id);
		}
	  });
	
}

function getNomUser(id, num)
{
	var nom;
	gapi.client.sictomApi.getUtilisateur({
      'id': id
    }).execute(function(resp) 
	{
		nom = resp.nom;
		switch(num)
		{
			case 1 :
				document.getElementById("nomChauffeur").innerHTML = nom;
				break;
			case 2 :
				document.getElementById("nomRipper1").innerHTML = nom;
				break;
			case 3 :
				document.getElementById("nomRipper2").innerHTML = nom;
				break;
			default :
				break;
		}
	});
	return nom;
	
}


function showTournees()
{
	document.getElementById("listeTournees").innerHTML = '<form name="formTournees">';
	document.getElementById("listeTournees").innerHTML += '<select id="anneeTournee"><option>2015</option><option>2014</option></select> ';
	document.getElementById("listeTournees").innerHTML += '<select id="moisTournee"><option>Janvier</option><option>F&eacute;vrier</option><option>Mars</option><option>Avril</option><option>Mai</option><option>Juin</option><option>Juillet</option><option>Aout</option><option>Septembre</option><option>Octobre</option><option>Novembre</option><option>D&eacute;cembre</option></select> ';
	//bouton validation
	document.getElementById("listeTournees").innerHTML += '<a href=# onclick="validateForm()"><input type="image" name="recherche_tournees" src="images/loupe.jpg" height="20" width="20"></a>';
	document.getElementById("listeTournees").innerHTML += "</form>";
}

function validateForm() {
  alert(document.getElementById("anneeTournee").text);
}

function showTrajet(idTournee)
{
	//Récupération des coordonnées
	gapi.client.sictomApi.gestCoordonneesByTournee({
      'id': idTournee
    }).execute(function(resp) 
	{
		var liste_coord = resp.items;
		for (var i=0; i < liste_coord.length - 2; i++)
		{
			var newLineCoordinates = [
			new google.maps.LatLng(liste_coord[i].latitude, liste_coord[i].longitude),
			new google.maps.LatLng(liste_coord[i+1].latitude, liste_coord[i+1].longitude)]
			var newLine = new google.maps.Polyline({
					path: newLineCoordinates,        
					strokeColor: "#FF0000",
					strokeOpacity: 1.0,
					strokeWeight: 2
				});
				newLine.setMap(map);
		}

	});
}