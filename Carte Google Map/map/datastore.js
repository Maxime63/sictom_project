//liste des camions
var camions;
var tournees;
var nomChauffeur;
var nomRipper2;
var tournee_cour;
var coord;
var distance_tournee;
var camionSelected;

function compareCamion(a,b) {
var nomRipper1;
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
				document.getElementById("MenuCamions").innerHTML += '<a href=# id="'+index+'"onclick="getTournee(this)">' + camions[index].nom +'</a><br />';
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
			for(var i=0; i <tournees.length; i++)
			{
				tournees[i].dateDebut = tournees[i].dateDebut.substring(0,10);				
				tournees[i].dateFin = tournees[i].dateFin.substring(0,10);
			}
			tournees.sort(compareTournee);
			//Revoir test
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
			camionSelected = camions[event.id].nom;;
			document.getElementById("nomCamion").innerHTML = camionSelected; 
			document.getElementById("dateDep").innerHTML = tournee_cour.dateDebut;
			document.getElementById("dateArr").innerHTML = tournee_cour.dateFin;
			
			showTrajet(tournee_cour.id);
		}
	  });
	
}

function afficherTournee(tournee_cour)
{
	document.getElementById('nomTournee').innerHTML = "Tourn&eacute;e du " + tournee_cour.dateDebut + " : " +  tournee_cour.nom;
	nomChauffeur = getNomUser(tournee_cour.chauffeurId,1);
	nomRipper1 = getNomUser(tournee_cour.firstRipperId,2);
	nomRipper2 = getNomUser(tournee_cour.secondRipperId,3);
	var div = document.getElementById("floatRight");
	div.style.visibility = 'visible';
	document.getElementById("nomCamion").innerHTML = camionSelected;
	document.getElementById("dateDep").innerHTML = tournee_cour.dateDebut;
	document.getElementById("dateArr").innerHTML = tournee_cour.dateFin;
			
	showTrajet(tournee_cour.id);
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
	document.getElementById("listeTournees").innerHTML += '<select id="moisTournee"><option>01</option><option>02</option><option>03</option><option>04</option><option>05</option><option>06</option><option>07</option><option>08</option><option>09</option><option>10</option><option>11</option><option>12</option></select> ';
	//bouton validation
	document.getElementById("listeTournees").innerHTML += '<a href=# onclick="validateForm()"><input type="image" name="recherche_tournees" src="images/loupe.jpg" height="15" width="15"></a>';
	document.getElementById("listeTournees").innerHTML += "</form><br />";
}

function getOldTournee(event)
{
	var tournee = tournees[event.id];
	afficherTournee(tournee);
}

function validateForm() {
  var annee = document.getElementById("anneeTournee").value;
  var mois = document.getElementById("moisTournee").value;
  for(var i=0; i<tournees.length; i++)
  {
	var anneeDeb = tournees[i].dateDebut.substring(0,4);
	if(annee == anneeDeb)
	{
		var moisDeb = tournees[i].dateDebut.substring(5,7);
		if(mois == moisDeb)
		{
			listeTournees
			document.getElementById("listeTournees").innerHTML += '<a href=# id="'+i+'" onclick="getOldTournee(this)">'+ tournees[i].nom +'</a><br />';
		}
	}
  }
}

function showTrajet(idTournee)
{
	//Récupération des coordonnées
	alert(idTournee);
	gapi.client.sictomApi.getCoordsByTournee({
      'tourneeId': idTournee
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



function initChart() 
{
	  var rootApi = 'https://speedy-baton-840.appspot.com/_ah/api'; 
	  gapi.client.load('sictomApi', 'v1', function() 
	  {
		gapi.client.sictomApi.getAllCoords().execute(function(resp) 
		{
		if (!resp.code) 
		{
			var index;
			coord = resp.items;
			afficherChart();			
		}
	  });
	}, rootApi);
	
}

function afficherChart()
{
      google.load('visualization', '1.0', {'packages':['corechart']});
	  drawChart();
  }
	  
	  
	  
	 function drawChart()
	{
		//var data = new google.visualization.DataTable();
		//data.addColumn('string', 'id tournee');
		//data.addColumn('number', 'nombre coordonnes');
		var tab_coord = new Object();
		//On ajoute dans un tableau associatif les distances parcourue
		for(var i=0; i<coord.length; i++)
		{
			if(!(coord[i].tourneeId in tab_coord))
			{
				tab_coord[coord[i].tourneeId] = 0;
			}
			else
			{
				tab_coord[coord[i].tourneeId] += 1;
			}
		}
		
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Tournee');
        data.addColumn('number', 'nb coord');
        data.addRows(tab_coord);
				
          var options = {'title':'Repartition enregistrements par tournee',
                       'width':400,
                       'height':300};

        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options);
	}

/*	function drawChartTournees() 
	{
	  
	  //Pour chaque tournée ++ tableau associatif
	  
	  //Création duu graphe
	  
      var data = new google.visualization.DataTable();
      data.addColumn('string', 'Topping');
      data.addColumn('number', 'Slices');
	  var camions_array = new Array();
	  for (var index = 0; index < camions.length; ++index) 
		{
			data.addRow([camions[index].nom, 1]);
		}		
		
      // Set chart options
      var options = {'title':'Nombre de tournees par camion',
                     'width':400,
                     'height':300};

      // Instantiate and draw our chart, passing in some options.
      var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
      chart.draw(data, options);
	}*/

	

	