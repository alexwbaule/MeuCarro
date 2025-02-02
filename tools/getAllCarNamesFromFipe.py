#!/usr/bin/env python3

import requests
import datetime
import locale
import sys
import hashlib

allcars = []

def getToday():
	locale.setlocale(locale.LC_ALL, "pt_BR.UTF-8")
	mydate = datetime.datetime.now()
	return mydate.strftime("%B/%Y ") # 'December'

def getReferency():
	r = requests.post("https://veiculos.fipe.org.br/api/veiculos/ConsultarTabelaDeReferencia", headers = {'Accept': 'application/json', 'Referer': 'https://veiculos.fipe.org.br/' , 'Origin': 'https://veiculos.fipe.org.br'})
	referencias = r.json()
	for referencia in referencias:
		if referencia['Mes'] == getToday():
			return referencia['Codigo']
	return None

def getAllReferency():
	r = requests.post("https://veiculos.fipe.org.br/api/veiculos/ConsultarTabelaDeReferencia", headers = {'Accept': 'application/json', 'Referer': 'https://veiculos.fipe.org.br/' , 'Origin': 'https://veiculos.fipe.org.br'})
	return r.json()

def getBrands(referencia, tipo):
	r = requests.post("https://veiculos.fipe.org.br/api/veiculos/ConsultarMarcas", data = {'codigoTabelaReferencia': referencia, 'codigoTipoVeiculo': tipo}, headers = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8','Accept': 'application/json', 'Referer': 'https://veiculos.fipe.org.br/' , 'Origin': 'https://veiculos.fipe.org.br'})
	brands = r.json()
	for brand in brands:
		getModelByBrand(referencia, tipo, brand['Value'], brand['Label'].upper())

def getModelByBrand(referencia, tipo, marca, marcanome):
	r = requests.post("https://veiculos.fipe.org.br/api/veiculos/ConsultarModelos", data = {'codigoTabelaReferencia': referencia, 'codigoTipoVeiculo': tipo, 'codigoMarca': marca }, headers = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8','Accept': 'application/json', 'Referer': 'https://veiculos.fipe.org.br/' , 'Origin': 'https://veiculos.fipe.org.br'})
	models = r.json()
	if 'Modelos' in models:
		for model in models['Modelos']:
			brand_model = "{} {}".format(marcanome, model['Label'].upper())
			if brand_model not in allcars:
				allcars.append(brand_model)

def getAll(file):
	sql_file = file + ".sql"
	sha_file = file + ".sha256"
	referency = getReferency()
	#print("Getting %s" % referency, file=sys.stderr)
	if referency != None:
		for tipo in [1, 2, 3]:
			#print("Getting Car type %d" % tipo, file=sys.stderr)
			getBrands(referency, tipo)
	else:
		print("Sem Data de refencia")
	
	h = hashlib.sha256()
	with open(sql_file,"w", encoding='utf-8') as file:
		file.write('{"inserts": [\n')
		for n,car in enumerate(allcars, start=1):
			insert = '"INSERT INTO cars_model (car) VALUES (\\"{}\\");"{}\n'.format(car, ("," if n != len(allcars) else ""))
			file.write(insert)
			h.update(insert.encode('utf-8'))
		file.write(']}')

	with open(sha_file,"w", encoding='utf-8') as file:
		file.write('{{"sha256\": "{}"}}'.format(h.hexdigest()))

if __name__ == "__main__":
	# Codigo Carros = 1
	# Codigo Motos = 2
	# Codigo Caminhão = 3
	if len(sys.argv) == 2:
		file = sys.argv[1]
		getAll(file)
