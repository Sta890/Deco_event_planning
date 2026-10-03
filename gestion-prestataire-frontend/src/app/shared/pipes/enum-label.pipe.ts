import { Pipe, PipeTransform } from '@angular/core';
import {
  MODE_PAIEMENT_LABELS,
  STATUT_DEVIS_LABELS,
  STATUT_FACTURE_LABELS,
  TYPE_EVENEMENT_LABELS,
} from '../enums';

/**
 * Table de libellés unique, alimentée par shared/enums.ts.
 * Volontairement typée en Record<string, string> : une valeur inconnue
 * (enum ajouté côté backend mais pas encore modélisé ici) ne doit pas
 * provoquer une erreur de compilation.
 */
export const ENUM_LABELS: Record<string, string> = {
  ...TYPE_EVENEMENT_LABELS,
  ...STATUT_DEVIS_LABELS,
  ...STATUT_FACTURE_LABELS,
  ...MODE_PAIEMENT_LABELS,
};

export function enumLabel(value: string | null | undefined): string {
  if (!value) {
    return '';
  }
  return ENUM_LABELS[value] ?? value;
}

@Pipe({
  name: 'enumLabel',
  standalone: true
})
export class EnumLabelPipe implements PipeTransform {

  transform(value: string | null | undefined): string {
    return enumLabel(value);
  }
}
